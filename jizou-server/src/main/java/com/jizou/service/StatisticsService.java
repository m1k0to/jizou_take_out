package com.jizou.service;

import com.jizou.vo.OrderStatisticsVO;
import com.jizou.vo.TurnOverStatisticsVO;
import com.jizou.vo.UserStatisticsVO;

import java.time.LocalDate;


public interface StatisticsService {

    /**
     * 获取指定时间范围的营业额数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    TurnOverStatisticsVO getTurnoverStatisticsData(LocalDate startDate, LocalDate endDate);

    /**
     * 获取指定时间范围的用户数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    UserStatisticsVO getUserStatisticsData(LocalDate startDate, LocalDate endDate);

    /**
     * 获取指定时间范围的订单数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    OrderStatisticsVO getOrdersStatisticsData(LocalDate startDate, LocalDate endDate);
}
