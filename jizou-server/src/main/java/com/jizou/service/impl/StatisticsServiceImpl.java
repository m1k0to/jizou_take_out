package com.jizou.service.impl;

import com.jizou.entity.Orders;
import com.jizou.mapper.OrderMapper;
import com.jizou.mapper.UserMapper;
import com.jizou.service.StatisticsService;
import com.jizou.vo.OrderStatisticsVO;
import com.jizou.vo.TurnOverStatisticsVO;
import com.jizou.vo.UserStatisticsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取指定时间范围的营业额数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public TurnOverStatisticsVO getTurnoverStatisticsData(LocalDate startDate, LocalDate endDate) {
        //  日期列表
        List<LocalDate> dateList = new ArrayList<>();
        //  营业额列表
        List<Double> turnoverList = new ArrayList<>();

        //  初始化日期列表
        dateList.add(startDate);

        while (!startDate.isEqual(endDate)) {
            startDate = startDate.plusDays(1);
            dateList.add(startDate);
        }

        //  遍历日期列表 计算当天营业额
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();

            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);

            //  查询结果 存入营业额列表
            Double turnover = orderMapper.turnoverOfTheDay(map);
            turnover = turnover == null ? 0.0 : turnover;

            turnoverList.add(turnover);

        }

        //  返回数据
        return TurnOverStatisticsVO.builder()
                .dateList(StringUtils.join(turnoverList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();

    }

    /**
     * 获取指定时间范围的用户数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public UserStatisticsVO getUserStatisticsData(LocalDate startDate, LocalDate endDate) {
        //  日期列表
        List<LocalDate> dateList = new ArrayList<>();
        //  总用户数量列表
        List<Integer> totalUserList = new ArrayList<>();
        //  新增用户数量列表
        List<Integer> newUserList = new ArrayList<>();

        //  生成日期列表
        dateList.add(startDate);

        while (!startDate.isEqual(endDate)) {
            startDate = startDate.plusDays(1);
            dateList.add(startDate);
        }

        //  初始化各列表
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("endTime", endTime);
            totalUserList.add(userMapper.usersOfTheDay(map));

            map.put("beginTime", beginTime);
            newUserList.add(userMapper.usersOfTheDay(map));
        }

        //  返回结果
        return UserStatisticsVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 获取指定时间范围的订单数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public OrderStatisticsVO getOrdersStatisticsData(LocalDate startDate, LocalDate endDate) {
        //  日期列表
        List<LocalDate> dateList = new ArrayList<>();
        //  总订单数量列表
        List<Integer> totalOrderList = new ArrayList<>();
        //  有效订单数量列表
        List<Integer> completedOrderList = new ArrayList<>();


        //  生成日期列表
        dateList.add(startDate);

        while (!startDate.isEqual(endDate)) {
            startDate = startDate.plusDays(1);
            dateList.add(startDate);
        }

        //  初始化各列表
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);

            totalOrderList.add(orderMapper.ordersOfTheDay(map));

            map.put("status", Orders.COMPLETED);
            completedOrderList.add(orderMapper.ordersOfTheDay(map));

        }

        //  完成订单和总订单数统计
        Integer completedOrdersCount = completedOrderList.stream().reduce(Integer::sum).get();
        Integer totalOrdersCount = totalOrderList.stream().reduce(Integer::sum).get();

        //  订单完成率统计
        Double orderCompletionRate = totalOrdersCount != 0 ? completedOrdersCount.doubleValue() / totalOrdersCount.doubleValue() : 0.0;

        //  返回结果
        return OrderStatisticsVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .validOrderCountList(StringUtils.join(completedOrderList, ","))
                .validOrderCount(completedOrdersCount)
                .orderCountList(StringUtils.join(totalOrderList, ","))
                .totalOrderCount(totalOrdersCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
}
