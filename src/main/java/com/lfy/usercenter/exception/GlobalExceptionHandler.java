package com.lfy.usercenter.exception;

import com.lfy.usercenter.common.BaseResponse;
import com.lfy.usercenter.model.enums.ErrorCode;
import com.lfy.usercenter.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，下面的注解用到了AOP
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 下面的注解的目的是针对BusinessException异常所作出的操作
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error(e.getMessage()+"BusinessException",e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(BusinessException e){
        log.error("RuntimeException",e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),e.getDescription());
    }
}
