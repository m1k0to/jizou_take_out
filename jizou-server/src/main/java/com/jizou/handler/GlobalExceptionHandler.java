package com.jizou.handler;

import com.jizou.constant.MessageConstant;
import com.jizou.exception.BaseException;
import com.jizou.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     *
     * 方法重载 捕获数据库异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        //    捕获错误信息
        String msg = ex.getMessage();
        //    当错误确定是约束错误时
        if (msg.contains("Duplicate entry")) {
            String[] split = msg.split(" ");
            String username = split[2];
            String errormsg = username + MessageConstant.ALREADY_EXISTS;
            return Result.error(errormsg);
        }else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
