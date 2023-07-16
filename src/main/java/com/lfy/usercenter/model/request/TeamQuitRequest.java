package com.lfy.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 队伍退出
 *
 * @author lfy
 */
@Data
public class TeamQuitRequest implements Serializable {

    private static final long serialVersionUID = -1167801642680919182L;

    /**
     * 队伍id
     */
    private Long teamId;

}
