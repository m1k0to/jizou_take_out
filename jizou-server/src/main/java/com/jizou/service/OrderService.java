package com.jizou.service;

import com.jizou.dto.OrdersSubmitDTO;
import com.jizou.vo.OrderSubmitVO;

public interface OrderService {

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO);
}
