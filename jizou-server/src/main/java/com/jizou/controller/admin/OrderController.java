package com.jizou.controller.admin;

import com.jizou.dto.OrdersCancelDTO;
import com.jizou.dto.OrdersConfirmDTO;
import com.jizou.dto.OrdersPageQueryDTO;
import com.jizou.dto.OrdersRejectionDTO;
import com.jizou.result.PageResult;
import com.jizou.result.Result;
import com.jizou.service.OrderService;
import com.jizou.vo.OrderCountVO;
import com.jizou.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "管理端订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 条件订单查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("条件订单查询")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("条件查询订单：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 统计各状态订单数量
     *
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态订单数量统计")
    public Result<OrderCountVO> orderCount() {
        OrderCountVO orderCountVO = orderService.orderCount();
        return Result.success(orderCountVO);
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("订单详情")
    public Result<OrderVO> orderDetails(@PathVariable("id") Long id) {
        OrderVO orderVO = orderService.getOrderDetails(id);
        return Result.success(orderVO);
    }

    /**
     * 订单接单
     *
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("订单接单")
    public Result orderConfirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        orderService.orderConfirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 订单拒单
     *
     * @param ordersRejectionDTO
     * @return
     * @throws Exception
     */
    @PutMapping("/rejection")
    @ApiOperation("订单拒单")
    public Result orderRejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO) throws Exception {
        orderService.orderRejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 订单取消
     *
     * @param ordersCancelDTO
     * @return
     * @throws Exception
     */
    @PutMapping("/cancel")
    @ApiOperation("订单取消")
    public Result orderCancel(@RequestBody OrdersCancelDTO ordersCancelDTO) throws Exception {
        orderService.orderCancelByAdmin(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     *
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("订单派送")
    public Result orderDelivery(@PathVariable("id") Long id) {
        orderService.orderDelivery(id);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("订单完成")
    public Result orderComplete(@PathVariable("id") Long id) {
        orderService.orderComplete(id);
        return Result.success();
    }

}
