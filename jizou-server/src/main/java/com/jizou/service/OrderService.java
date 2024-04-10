package com.jizou.service;

import com.jizou.dto.*;
import com.jizou.result.PageResult;
import com.jizou.vo.OrderPaymentVO;
import com.jizou.vo.OrderCountVO;
import com.jizou.vo.OrderSubmitVO;
import com.jizou.vo.OrderVO;

public interface OrderService {

    /**
     * 用户提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 用户支付订单
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO orderPayment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功时修改订单状态
     *
     * @param outTradeNo
     */
    void payWithSuccess(String outTradeNo);

    /**
     * 历史订单查询
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    PageResult pageQuery(int pageNum, int pageSize, Integer status);

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    OrderVO getOrderDetails(Long id);

    /**
     * 用户取消订单
     *
     * @param id
     */
    void orderCancelByUser(Long id) throws Exception;

    /**
     * 商家取消订单
     *
     * @param ordersCancelDTO
     */
    void orderCancelByAdmin(OrdersCancelDTO ordersCancelDTO) throws Exception;

    /**
     * 用户再来一单
     *
     * @param id
     */
    void orderRepetition(Long id);

    /**
     * 条件订单查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各种状态订单数量统计
     *
     * @return
     */
    OrderCountVO orderCount();

    /**
     * 订单接单
     *
     * @param ordersConfirmDTO
     */
    void orderConfirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 订单拒单
     *
     * @param ordersRejectionDTO
     */
    void orderRejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    /**
     * 订单派送
     *
     * @param id
     */
    void orderDelivery(Long id);

    /**
     * 订单完成
     *
     * @param id
     */
    void orderComplete(Long id);

    /**
     * 用户催单
     *
     * @param id
     */
    void orderReminder(Long id);
}
