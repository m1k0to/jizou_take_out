package com.jizou.service;

import com.jizou.vo.TurnoverReportVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


public interface StatisticsService {

    /**
     * 获取指定时间范围的营业额数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    TurnoverReportVO getTurnoverStatisticsData(LocalDate startDate, LocalDate endDate);
}
