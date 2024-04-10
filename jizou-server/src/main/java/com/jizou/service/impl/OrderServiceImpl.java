package com.jizou.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jizou.constant.MessageConstant;
import com.jizou.context.BaseContext;
import com.jizou.dto.*;
import com.jizou.entity.*;
import com.jizou.exception.AddressBookBusinessException;
import com.jizou.exception.OrderBusinessException;
import com.jizou.exception.ShoppingCartBusinessException;
import com.jizou.mapper.*;
import com.jizou.result.PageResult;
import com.jizou.service.OrderService;
import com.jizou.utils.HttpClientUtil;
import com.jizou.utils.WeChatPayUtil;
import com.jizou.vo.OrderPaymentVO;
import com.jizou.vo.OrderStatisticsVO;
import com.jizou.vo.OrderSubmitVO;
import com.jizou.vo.OrderVO;
import com.jizou.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;

    @Value("${jizou.shop.address}")
    private String shopAddress;

    @Value("${jizou.baidu.ak}")
    private String ak;

    @Value("${jizou.baidu.latlng-url}")
    private String latlngUrl;

    @Value("${jizou.baidu.routing-url}")
    private String routingUrl;

    /**
     * 用户提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO) {

        //  处理业务异常
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());

        //  抛出地址业务异常
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        String deliveryAddress = addressBook.getProvinceName() + addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail();

        //  检查地址是否超出配送范围
        checkAddress(deliveryAddress);

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if (list == null || list.isEmpty()) {
            //  抛出购物车业务异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //  订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        orders.setAddress(deliveryAddress);
        orderMapper.insert(orders);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        //  订单明细表插入n条数据
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }

        orderDetailMapper.insertBatch(orderDetailList);

        //  清空购物车
        shoppingCartMapper.deleteByUserId(userId);

        //  封装VO返回数据
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        return orderSubmitVO;
    }

    /**
     * 用户支付订单
     *
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO orderPayment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        /*//调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );*/

        JSONObject jsonObject = new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }


        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功时修改订单状态
     *
     * @param outTradeNo
     */
    @Override
    public void payWithSuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        //  更新数据
        orderMapper.update(orders);

        //  WebSocket向客户端推送数据
        Map map = new HashMap<>();
        map.put("type", 1);
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号: " + outTradeNo);

        //  封装为json数据
        String jsonString = JSONObject.toJSONString(map);
        webSocketServer.sendToAllClient(jsonString);

    }

    /**
     * 历史订单查询
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult pageQuery(int pageNum, int pageSize, Integer status) {
        //  设置分页
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();

        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        //  条件分页查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();

        //  逐条查询订单明细并装入
        if (page != null && !page.isEmpty()) {
            for (Orders orders : page) {
                Long orderId = orders.getId();
                List<OrderDetail> details = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(details);

                list.add(orderVO);
            }
        }

        return new PageResult(page.getTotal(), list);

    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetails(Long id) {
        //  查询订单信息
        Orders order = orderMapper.getById(id);

        //  查询订单明细
        List<OrderDetail> details = orderDetailMapper.getByOrderId(id);

        //  封装数据
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(details);

        return orderVO;
    }

    /**
     * 用户取消订单
     *
     * @param id
     */
    @Override
    public void orderCancelByUser(Long id) throws Exception {
        //  获取对应订单信息
        Orders order = orderMapper.getById(id);

        //  判断订单是否存在
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //  判断订单状态
        if (order.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //  用户已付款 需要退款
        if (order.getPayStatus().equals(Orders.PAID)) {
            /*weChatPayUtil.refund(
                    order.getNumber(),
                    order.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01)

            );*/
        }

        Orders orders = Orders.builder()
                .id(id)
                .payStatus(Orders.REFUND)
                .status(Orders.CANCELLED)
                .cancelReason("用户取消订单")
                .cancelTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

    }

    /**
     * 商家取消订单
     *
     * @param ordersCancelDTO
     */
    @Override
    public void orderCancelByAdmin(OrdersCancelDTO ordersCancelDTO) throws Exception {
        Long orderId = ordersCancelDTO.getId();

        Orders order = orderMapper.getById(orderId);

        if (order.getPayStatus().equals(Orders.PAID)) {
            //调用微信支付接口退款逻辑
        }

        Orders orders = Orders.builder()
                .id(orderId)
                .status(Orders.CANCELLED)
                .payStatus(Orders.REFUND)
                .cancelReason(ordersCancelDTO.getCancelReason())
                .cancelTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 用户再来一单
     *
     * @param id
     */
    @Override
    public void orderRepetition(Long id) {
        //  获取用户id
        Long userId = BaseContext.getCurrentId();

        //  获取订单详细信息
        List<OrderDetail> details = orderDetailMapper.getByOrderId(id);

        //  将订单中的菜品放入购物车
        List<ShoppingCart> shoppingCartList = details.stream().map(detail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(detail, shoppingCart);

            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 条件订单查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();

        //  逐条查询订单明细并装入
        if (page != null && !page.isEmpty()) {
            for (Orders orders : page) {
                Long orderId = orders.getId();
                List<OrderDetail> details = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);

                //  将订单菜品详细信息封装并存入
                String orderDishes = String.join("", details.stream().map(detail -> {
                    String orderDish = detail.getName() + "*" + detail.getNumber() + ";";
                    return orderDish;
                }).collect(Collectors.toList()));

                orderVO.setOrderDishes(orderDishes);
                list.add(orderVO);
            }
        }

        return new PageResult(page.getTotal(), list);
    }

    /**
     * 各种状态订单数量统计
     *
     * @return
     */
    @Override
    public OrderStatisticsVO orderStatistics() {
        //  根据状态分别统计不同状态的订单数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);

        return orderStatisticsVO;
    }

    /**
     * 订单接单
     *
     * @param ordersConfirmDTO
     */
    @Override
    public void orderConfirm(OrdersConfirmDTO ordersConfirmDTO) {

        Orders orders = Orders.builder()
                .id(ordersConfirmDTO.getId())
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(orders);

    }

    /**
     * 订单拒单
     *
     * @param ordersRejectionDTO
     */
    @Override
    public void orderRejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {

        Long orderId = ordersRejectionDTO.getId();

        //  根据id查询订单
        Orders order = orderMapper.getById(orderId);

        //  判断订单状态
        //  只有处于待接单状态的订单可以拒单
        if (order == null || !order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new RuntimeException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //  退单的订单已经支付 因此需要调用微信支付接口退款
        if (order.getPayStatus().equals(Orders.PAID)) {
            /*weChatPayUtil.refund(
                    order.getNumber(),
                    order.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01)

            );*/
        }

        //  更新拒单时间 拒单理由 订单状态
        Orders orders = Orders.builder()
                .id(orderId)
                .status(Orders.CANCELLED)
                .payStatus(Orders.REFUND)
                .rejectionReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 订单派送
     *
     * @param id
     */
    @Override
    public void orderDelivery(Long id) {

        Orders order = orderMapper.getById(id);

        //  只用已经接单且未派送的订单才可修改
        if (order == null || !order.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();

        orderMapper.update(orders);
    }

    /**
     * 订单完成
     *
     * @param id
     */
    @Override
    public void orderComplete(Long id) {

        Orders order = orderMapper.getById(id);

        //  只有派送中的订单才可修改为已完成
        if (order == null || !order.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.COMPLETED)
                .deliveryTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    private void checkAddress(String address) {
        Map map = new HashMap<>();
        map.put("address", shopAddress);
        map.put("output", "json");
        map.put("ak", ak);

        //  获取店铺经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet(latlngUrl, map);
        JSONObject jsonObject = JSONObject.parseObject(shopCoordinate);
        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException(MessageConstant.SHOP_ADDRESS_ANALYSE_ERROR);
        }

        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String shopLatLng = new StringBuilder().append(location.getString("lat") + "," + location.getString("lng")).toString();

        //  获取配送地址经纬坐标
        map.put("address", address);
        String userCoordinate = HttpClientUtil.doGet(latlngUrl, map);
        jsonObject = JSONObject.parseObject(userCoordinate);
        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException(MessageConstant.USER_ADDRESS_ANALYSE_ERROR);
        }

        location = jsonObject.getJSONObject("result").getJSONObject("location");
        String userLatLng = new StringBuilder().append(location.getString("lat") + "," + location.getString("lng")).toString();

        //  获取规划路线
        map.put("origin", shopLatLng);
        map.put("destination", userLatLng);
        map.put("steps_info", "0");

        String routing = HttpClientUtil.doGet(routingUrl, map);
        jsonObject = JSONObject.parseObject(routing);
        if (!jsonObject.getString("status").equals("0")) {
            throw new OrderBusinessException(MessageConstant.DELIVERY_ROUTING_FINDING_ERROR);
        }

        //  计算路线距离并判断是否超出范围
        Integer distance = (Integer) ((JSONObject) jsonObject.getJSONObject("result").getJSONArray("routes").get(0)).get("distance");

        if (distance > 5000) {
            throw new OrderBusinessException(MessageConstant.OUT_OF_RANGE);
        }
    }
}
