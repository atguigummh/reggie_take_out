package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1、将也页面提交的密码password进行md5加密处理
        String password =employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);
        //3、如果没有查询到则返回登录失败结果
        if (emp==null){
            return R.error("未查到该用户名");
        }
        //4、比对密码，如果不一致返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("密码不一致");
        }
        //5、查看员工状态。如果为已禁用，则返回员工已禁用结果
        if (emp.getStatus()==0){
            return R.error("员工状态已禁用");

        }
        //登录成功。将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 退出功能
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());
        //设置初始密码123456,需要进行md5的加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置当前用户创建和修改时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //获得当前登录用户的id
        //Long empid = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empid);
        //employee.setUpdateUser(empid);
        employeeService.save(employee);
        return R.success("新增员工成功");

    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo=new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper();
        //添加过滤条件
        wrapper.like(StringUtils.hasText(name),Employee::getName,name);
        //添加排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,wrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info("修改：{}员工状态",employee.toString());
        long id = Thread.currentThread().getId();
        log.info("线程id为:{}",id);
        //设置当前修改的时间
        //employee.setUpdateTime(LocalDateTime.now());
        //设置是谁修改的
        //employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        //调用sql修改状态
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息.... ");
        Employee employee = employeeService.getById(id);
        if (employee!=null){
        return R.success(employee);
    }
        return R.error("没有查询到对应员工信息");
    }
}
