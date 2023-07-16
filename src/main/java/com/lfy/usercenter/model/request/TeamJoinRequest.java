package com.lfy.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 队伍加入
 */
@Data
public class TeamJoinRequest implements Serializable {

    private static final long serialVersionUID = 2663795366763958487L;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;

}
