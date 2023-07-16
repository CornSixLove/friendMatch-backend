package com.lfy.usercenter.utils;

import com.lfy.usercenter.common.BaseResponse;
import com.lfy.usercenter.model.enums.ErrorCode;

/**
 * 生成返回的对象工具类
 * @author lfy
 */
public class ResultUtils {
    /**
     * 成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<T>(0,data,"success");
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse(errorCode);
    }

    public static BaseResponse error(ErrorCode errorCode,String description){
        return new BaseResponse(errorCode.getCode(),null,description);
    }

    public static BaseResponse error(ErrorCode errorCode,String message,String description){
        return new BaseResponse(errorCode.getCode(),null,message,description);
    }

    public static BaseResponse error(int code,String message,String description){
        return new BaseResponse(code,null,message,description);
    }
}
