package com.jizou.controller.user;

import com.jizou.dto.OrdersPaymentDTO;
import com.jizou.dto.OrdersSubmitDTO;
import com.jizou.result.PageResult;
import com.jizou.result.Result;
import com.jizou.service.OrderService;
import com.jizou.vo.OrderPaymentVO;
import com.jizou.vo.OrderSubmitVO;
import com.jizou.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@Slf4j
@RequestMapping("/user/order")
@Api(tags = "小程序订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单接口
     *
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户提交订单")
    public Result<OrderSubmitVO> orderSubmit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户提交订单：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.orderSubmit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("用户订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("用户订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.orderPayment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);

        //  模拟交易成功
        log.info("模拟交易成功: {}", ordersPaymentDTO.getOrderNumber());
        orderService.payWithSuccess(ordersPaymentDTO.getOrderNumber());

        return Result.success(orderPaymentVO);
    }

    /**
     * 历史订单查询
     *
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> page(int page, int pageSize, Integer status) {
        log.info("历史订单查询: page={}, pageSize={}, status={}", page, pageSize, status);
        PageResult result = orderService.pageQuery(page, pageSize, status);
        ;
        return Result.success(result);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> orderDetails(@PathVariable("id") Long id) {
        log.info("查询订单详情: {}", id);
        OrderVO orderVO = orderService.getOrderDetails(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     *
     * @param id
     * @return
     * @throws Exception
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("用户取消订单")
    public Result orderCancel(@PathVariable("id") Long id) throws Exception {
        log.info("用户取消订单: {}", id);
        orderService.orderCancelByUser(id);
        return Result.success();
    }

    /**
     * 用户再来一单
     *
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("用户再来一单")
    public Result orderRepetition(@PathVariable("id") Long id) {
        log.info("用户再来一单: {}", id);
        orderService.orderRepetition(id);
        return Result.success();
    }
}
