package com.jizou.service;

import com.jizou.dto.EmployeeDTO;
import com.jizou.dto.EmployeeLoginDTO;
import com.jizou.dto.EmployeePageQueryDTO;
import com.jizou.entity.Employee;
import com.jizou.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 员工新增
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用/禁用员工账号
     *
     * @param status
     * @param id
     * @return
     */
    void changeStatus(Integer status, Long id);

    /**
     * 根据id获取员工信息
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);
}
