package com.jizou.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jizou.constant.MessageConstant;
import com.jizou.constant.PasswordConstant;
import com.jizou.constant.StatusConstant;
import com.jizou.context.BaseContext;
import com.jizou.dto.EmployeeDTO;
import com.jizou.dto.EmployeeLoginDTO;
import com.jizou.dto.EmployeePageQueryDTO;
import com.jizou.entity.Employee;
import com.jizou.exception.AccountLockedException;
import com.jizou.exception.AccountNotFoundException;
import com.jizou.exception.PasswordErrorException;
import com.jizou.mapper.EmployeeMapper;
import com.jizou.result.PageResult;
import com.jizou.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //  密码比对
        //  对前端传过来的密码进行MD5加密处理 再进行比较
        //  由于MD5只能单向加密 因此 数据库中的密码也需要修改
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 员工新增
     *
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {
        //  新建实体对象
        Employee employee = new Employee();

        //  若两类对象属性名字相同 则可进行属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //  设置帐号状态 默认启用
        employee.setStatus(StatusConstant.ENABLE);

        //  设置密码 默认123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //  设置该条记录的创建和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //  设置当前记录创建人id和修改人id
        //  利用线程常量
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        //  插入员工信息至数据库
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //  使用PageHelper类进行分页
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        //  使用Page类查询结果
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        //  获取参数
        long total = page.getTotal();
        List<Employee> records = page.getResult();
        //  返回结果
        return new PageResult(total, records);
    }

    /**
     * 启用/禁用员工账号
     *
     * @param status
     * @param id
     * @return
     */
    public void changeStatus(Integer status, Long id) {

        //创建查询对象 使用构造器
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        employeeMapper.update(employee);
    }

    /**
     * 根据id获取员工信息
     *
     * @param id
     * @return
     */
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("******");
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    public void update(EmployeeDTO employeeDTO){
        //  拷贝更新信息
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);

        //  必要数据更新
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());

        //  更改数据库使生效
        employeeMapper.update(employee);

    }
}
