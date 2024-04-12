package com.jizou.service;

import com.jizou.vo.OrderStatisticsVO;
import com.jizou.vo.SalesTop10ReportVO;
import com.jizou.vo.TurnOverStatisticsVO;
import com.jizou.vo.UserStatisticsVO;

import javax.servlet.http.HttpServletResponse;
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

    /**
     * 获取指定时间范围销量Top10菜品数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    SalesTop10ReportVO getSalesTop10StatisticsData(LocalDate startDate, LocalDate endDate);

    /**
     * 获取运营数据报表
     *
     * @param response
     */
    void getExportedSheet(HttpServletResponse response);
}
