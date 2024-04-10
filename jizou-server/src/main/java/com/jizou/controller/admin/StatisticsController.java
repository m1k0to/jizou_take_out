package com.jizou.controller.admin;

import com.jizou.result.Result;
import com.jizou.service.StatisticsService;
import com.jizou.vo.OrderStatisticsVO;
import com.jizou.vo.TurnOverStatisticsVO;
import com.jizou.vo.UserStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "管理端统计接口")
@Slf4j
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;


    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnOverStatisticsVO> shopTurnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额统计: {}, {}", begin, end);
        return Result.success(statisticsService.getTurnoverStatisticsData(begin, end));
    }

    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserStatisticsVO> shopUserStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end)
    {
        log.info("用户统计: {}, {}", begin, end);
        return Result.success(statisticsService.getUserStatisticsData(begin, end));
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderStatisticsVO> shopOrderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end)
    {
        log.info("订单统计: {}, {}", begin, end);
        return Result.success(statisticsService.getOrdersStatisticsData(begin, end));
    }

}
