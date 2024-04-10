package com.jizou.mapper;


import com.github.pagehelper.Page;
import com.jizou.dto.OrdersPageQueryDTO;
import com.jizou.entity.Orders;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper

public interface OrderMapper {

    /**
     * 插入订单数据
     *
     * @param orders
     */
    void insert(Orders orders);


    /**
     * 更新订单信息
     *
     * @param orders
     */
    void update(Orders orders);

    /**
     * 分页查询历史订单 按下单时间排序
     *
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据订单号查询订单
     *
     * @param outTradeNo
     * @return
     */
    @Select("select * from orders where number = #{outTradeNo}")
    Orders getByNumber(String outTradeNo);

    /**
     * 根据订单id查询订单
     *
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 统计对应状态的订单数量
     *
     * @param status
     * @return
     */
    @Select("select count(id) from orders where status = #{status} ")
    Integer countStatus(Integer status);

    /**
     * 根据订单状态和下单时间查询订单
     *
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time = #{orderTime} ")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime orderTime);

    /**
     * 统计当天营业额
     *
     * @param map
     * @return
     */
    Double turnoverOfTheDay(Map map);

    /**
     * 根据条件动态统计订单数量
     * @param map
     * @return
     */
    Integer ordersOfTheDay(Map map);
}
