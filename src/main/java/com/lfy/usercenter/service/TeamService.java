package com.lfy.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lfy.usercenter.model.domain.Team;
import com.lfy.usercenter.model.domain.User;
import com.lfy.usercenter.model.dto.TeamQuery;
import com.lfy.usercenter.model.request.TeamJoinRequest;
import com.lfy.usercenter.model.request.TeamQuitRequest;
import com.lfy.usercenter.model.request.TeamUpdateRequest;
import com.lfy.usercenter.model.vo.TeamUserVO;

import java.util.List;


/**
* @author 13155
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-06-23 12:13:19
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery,boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 解散队伍
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);
}
