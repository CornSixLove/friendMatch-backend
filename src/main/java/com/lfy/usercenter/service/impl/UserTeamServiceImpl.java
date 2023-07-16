package com.lfy.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lfy.usercenter.mapper.UserTeamMapper;
import com.lfy.usercenter.model.domain.UserTeam;
import com.lfy.usercenter.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author 13155
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-06-23 12:16:29
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




