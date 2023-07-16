package com.lfy.usercenter.common;

import com.lfy.usercenter.model.enums.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * @param <T>因为data要提升通用性，因此使用反省就可保证其他方法调用该方法时返回的数据都能被被封装
 * @author lfy
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message,String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
        this.message = "";
        this.description = "";
    }

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = "";
    }

    public BaseResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.data = null;
        this.message = errorCode.getMessage();
        this.description = errorCode.getDescription();
    }

    public BaseResponse(ErrorCode errorCode,String description) {
        this.code = errorCode.getCode();
        this.data = null;
        this.message = errorCode.getMessage();
        this.description = description;
    }

    public BaseResponse(ErrorCode errorCode,String message,String description) {
        this.code = errorCode.getCode();
        this.data = null;
        this.message = message;
        this.description = description;
    }
}
