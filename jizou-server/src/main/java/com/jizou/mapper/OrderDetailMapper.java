package com.jizou.mapper;

import com.jizou.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单明细信息
     *
     * @param orderDetailList
     */
    void insertBatch(List<OrderDetail> orderDetailList);
}
