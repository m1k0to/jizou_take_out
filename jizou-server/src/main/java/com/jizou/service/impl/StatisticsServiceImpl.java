package com.jizou.service.impl;

import com.jizou.entity.Orders;
import com.jizou.mapper.OrderMapper;
import com.jizou.service.StatisticsService;
import com.jizou.vo.TurnoverReportVO;
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

    /**
     * 获取指定时间范围的营业额数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatisticsData(LocalDate startDate, LocalDate endDate) {
        //  日期集合
        List<LocalDate> dateList = new ArrayList<>();
        //  营业额集合
        List<Double> turnoverList = new ArrayList<>();

        //  生成日期列表
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

            Double turnover = orderMapper.turnoverOfTheDay(map);
            turnover = turnover == null ? 0.0 : turnover;

            turnoverList.add(turnover);

        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(turnoverList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();

    }
}
