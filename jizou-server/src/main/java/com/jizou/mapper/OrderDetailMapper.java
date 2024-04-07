package com.jizou.mapper;

import com.jizou.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单明细信息
     *
     * @param orderDetailList
     */
    void insertBatch(List<OrderDetail> orderDetailList);


    /**
     * 根据订单id查询订单详情
     *
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
