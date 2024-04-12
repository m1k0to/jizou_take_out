package com.jizou.service.impl;

import com.jizou.dto.GoodsSalesDTO;
import com.jizou.entity.Orders;
import com.jizou.mapper.OrderMapper;
import com.jizou.mapper.UserMapper;
import com.jizou.service.StatisticsService;
import com.jizou.service.WorkspaceService;
import com.jizou.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

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
                .dateList(StringUtils.join(dateList, ","))
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

    /**
     * 获取指定时间范围销量Top10菜品数据
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10StatisticsData(LocalDate startDate, LocalDate endDate) {

        //  初始化开始结束日期
        LocalDateTime beginTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(endDate, LocalTime.MAX);

        //  准备数据
        Map map = new HashMap<>();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        map.put("status", Orders.COMPLETED);

        //  查询Top10热销菜品
        List<GoodsSalesDTO> salestop10List = orderMapper.top10OfTheDay(map);

        List<String> nameList = salestop10List.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = salestop10List.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        //  封装数据并返回
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }

    /**
     * 获取运营数据报表
     *
     * @param response
     */
    @Override
    public void getExportedSheet(HttpServletResponse response) {

        LocalDate now = LocalDate.now();

        LocalDate beginDay = now.minusDays(30);
        LocalDate endDay = now.minusDays(1);

        LocalDateTime beginTime = LocalDateTime.of(beginDay, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(endDay, LocalTime.MAX);
        //  查询数据
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);

        //  读取模板
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //   基于模板文件创建Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //  获取标签页
            XSSFSheet sheet1 = excel.getSheet("Sheet1");

            //  填充数据
            sheet1.getRow(1).getCell(1).setCellValue("时间：" + beginDay + " 至 " + endDay);

            XSSFRow row = sheet1.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            row = sheet1.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            //  根据日期 逐行查询并填充数据
            for (int i = 0; i < 30; i++) {

                LocalDate nowI = now.minusDays(30 - i);
                BusinessDataVO data = workspaceService.getBusinessData(LocalDateTime.of(nowI, LocalTime.MIN), LocalDateTime.of(nowI, LocalTime.MAX));

                row = sheet1.getRow(7 + i);

                row.getCell(1).setCellValue(nowI.toString());
                row.getCell(2).setCellValue(data.getTurnover());
                row.getCell(3).setCellValue(data.getValidOrderCount());
                row.getCell(4).setCellValue(data.getOrderCompletionRate());
                row.getCell(5).setCellValue(data.getUnitPrice());
                row.getCell(6).setCellValue(data.getNewUsers());

            }

            //  数据写入文件
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            //  释放资源
            outputStream.close();
            excel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
