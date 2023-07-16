package com.lfy.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 登陆
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -6920741058658801832L;

    private String userAccount;

    private String userPassword;

}
