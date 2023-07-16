package com.lfy.usercenter.service;

import com.lfy.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 13155
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-03-31 16:42:10
*/
public interface UserService extends IService<User> {
    /**
     *
     * @param userAccount 校验传入的账号
     * @param userPassword 校验的密码
     * @param checkPassword 再次确认密码
     * @param planetCode 用户编号
     * @return 校验成功的用户id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword,String planetCode);

    /**
     * 用户登陆
     * @param userAccount
     * @param userPassword
     * @return 返回脱敏用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏返回安全信息
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     * @param tagNameList
     * @return
     */
    List<User> searchUserByTags(List<String> tagNameList);

    /**
     * 修改用户数据
     * @param user
     * @return
     */
    int updateUser(User user,User loginUser);

    /**
     * 获取当前用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 通过request来判断当前用户是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 通过传入的对象来判断当前用户是否为管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);

}
