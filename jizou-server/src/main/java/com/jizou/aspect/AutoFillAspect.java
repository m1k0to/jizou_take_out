package com.jizou.aspect;

/*
    *   自定义切面类 实现公共字段自动填充
 */

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /*
        *   切入点
        *   指定类的方法执行 && 具有AutoFill注解
     */
    @Pointcut("execution(* com.jizou.mapper.*.*(..)) && @annotation(com.jizou.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /*
        *   前置通知 在通知中进行公共字段赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段填充...");
        
    }

}
