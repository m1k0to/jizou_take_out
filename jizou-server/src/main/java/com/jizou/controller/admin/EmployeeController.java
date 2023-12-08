package com.jizou.controller.admin;

import com.jizou.constant.JwtClaimsConstant;
import com.jizou.dto.EmployeeDTO;
import com.jizou.dto.EmployeeLoginDTO;
import com.jizou.dto.EmployeePageQueryDTO;
import com.jizou.entity.Employee;
import com.jizou.properties.JwtProperties;
import com.jizou.result.PageResult;
import com.jizou.result.Result;
import com.jizou.service.EmployeeService;
import com.jizou.utils.JwtUtil;
import com.jizou.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder().id(employee.getId()).userName(employee.getUsername()).name(employee.getName()).token(token).build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工登出")
    public Result<String> logout() {
        return Result.success();
    }


    @PostMapping
    @ApiOperation(value = "保存员工信息")
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        //    打印日志
        log.info("新增员工：{}", employeeDTO);
        //    新增员工信息并存储
        employeeService.save(employeeDTO);
        //    返回结果
        return Result.success();
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "查询员工信息")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        //  输出信息
        log.info("员工分页查询，参数{}", employeePageQueryDTO);
        //  存储查询结果
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 启用/禁用员工账号
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用员工账号")
    public Result changeStatus(@PathVariable Integer status, Long id) {
        log.info("启用/禁用员工账号：{}, {}", status, id);
        employeeService.changeStatus(status, id);
        return Result.success();
    }

    /**
     * 根据id获取员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id获取员工信息")
    public Result<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        employeeService.update(employeeDTO);
        return Result.success();
    }

}