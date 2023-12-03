package com.jizou.annotation;

/*
    *   自定义注解 用于表示某个方法需要进行字段自动填充处理
 */

import com.jizou.enumeration.OperationType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {

    /**
     * 获取数据库操作类型
     * @return
     */
    OperationType value();
}
