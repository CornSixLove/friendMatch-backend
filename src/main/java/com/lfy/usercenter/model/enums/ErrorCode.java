package com.lfy.usercenter.model.enums;

import lombok.Data;

/**
 * 全局错误码
 */
public enum ErrorCode {

    SUCCESS(0,"success",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"数据为空",""),
    NOT_LOGIN(40100,"没有登陆",""),
    NO_AUTH(40101,"没有权限",""),
    USER_EXIST(40200,"用户已存在",""),
    USER_NULL(40300,"用户不存在",""),
    FORBIDDEN(40301,"禁止访问",""),
    TIME_OUT(50100,"超时",""),
    SYSTEM_ERROR(50000,"系统内部异常","");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 详情
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
