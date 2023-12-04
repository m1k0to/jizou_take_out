package com.jizou.aspect;

/*
 *   自定义切面类 实现公共字段自动填充
 */

import com.jizou.annotation.AutoFill;
import com.jizou.constant.AutoFillConstant;
import com.jizou.context.BaseContext;
import com.jizou.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /*
     *   切入点
     *   指定类的方法执行 && 具有AutoFill注解
     */
    @Pointcut("execution(* com.jizou.mapper.*.*(..)) && @annotation(com.jizou.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    /*
     *   前置通知 在通知中进行公共字段赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws Exception {
        log.info("开始进行公共字段填充...");

        //  获得方法签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //  获得方法上的注解对象
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);
        //  判断当前被拦截方法数据库操作类型
        OperationType operationTypeValue = annotation.value();

        //  获取当前被拦截方法参数（实体对象）
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object object = args[0];

        //  准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //  根据操作类型 为对应的属性通过反射赋值
        if (operationTypeValue == OperationType.INSERT) {
            Method setCreateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setCreateTime.invoke(object, now);
            setCreateUser.invoke(object, currentId);
            setUpdateTime.invoke(object, now);
            setUpdateUser.invoke(object, currentId);
        } else if (operationTypeValue == OperationType.UPDATE) {
            Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setUpdateTime.invoke(object, now);
            setUpdateUser.invoke(object, currentId);
        }


    }

}
