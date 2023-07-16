package com.lfy.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lfy.usercenter.model.enums.ErrorCode;
import com.lfy.usercenter.exception.BusinessException;
import com.lfy.usercenter.mapper.UserMapper;
import com.lfy.usercenter.model.domain.User;
import com.lfy.usercenter.service.UserService;
import com.lfy.usercenter.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.lfy.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.lfy.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author lfy
* @description 针对表【user(用户)】的数据库操作Service实现
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    /**
     * SALT 混淆密码
     */
    private static final String SALT = "lfy";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {
        //1.校验,StringUtils是*commons-lang3*插件提供的功能
        //能够判断这些字符串是否为空
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"字符串不应为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度应该大于四");
        }
        if(userPassword.length()<8 || checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码应该大于8");
        }
        if(planetCode.length()>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户码不大于5");
        }
        //账户不能包含特殊字符(正则表达式)
        String ValidPattern = ".*[\\s`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*";
        Matcher matcher = Pattern.compile(ValidPattern).matcher(userAccount);
        if(matcher.matches()){
            //存在特殊字符
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"存在特殊字符");
        }
        if(!userPassword.equals(checkPassword)){
            //密码和校验密码不一致
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验密码不一致");
        }

        //账户不能重复
        //queryWrapper该对象封装了MybatisPlus的动态查询方法
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        //此处的this指的是当前的对象
        long count = userMapper.selectCount(queryWrapper);
        //说明数据库中已经存在相同的账户了
        if(count>0){
            throw new BusinessException(ErrorCode.USER_EXIST,"用户已存在");
        }

        //用户编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode",planetCode);
        count = userMapper.selectCount(queryWrapper);
        //说明数据库中已经存在相同的账户了
        if(count>0){
            throw new BusinessException(ErrorCode.USER_EXIST,"用户已存在");
        }

        //2.加密,SALT相当于加了一串密钥让密码更难解密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3.添加到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        //数据库的密码一定要存放加密之后的
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if(!saveResult){
            //因为id定义的Long，而该方法返回的是long因此要避免出现null导致拆箱错误
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"后台保存异常");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"账户或者密码不能为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户不能小于4");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不能小于8");
        }

        //账户不能包含特殊字符(正则表达式)
        String ValidPattern = ".*[\\s`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*";
        Matcher matcher = Pattern.compile(ValidPattern).matcher(userAccount);
        if(matcher.matches()){
            //存在特殊字符
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户不能包含特殊字符");
        }

        //2.加密,SALT相当于加了一串密钥让密码更难解密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //账户不能重复
        //queryWrapper该对象封装了MybatisPlus的动态查询方法
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);

        //用户为空
        if(user==null){
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.USER_NULL,"用户不存在");
        }

        //3.用户脱敏
        User safetyUser = getSafetyUser(user);

        //4.记录用户的登陆状态
        /**
         * 过程
         * 1.链接服务端后得到一个session1会话状态，返回给前端
         * 2.登陆成功后，得到一个登陆成功的session，返回给前端一个cookie命令
         * 3.前端接收到后端的命令后，设置cookie，保存到浏览器内
         * 4.前端再次请求后端的时候（相同的域名），在请求头中带上cookie去请求
         * 5.后端拿到前端传来的cookie，找到相应的session
         * 6.后端从session中可以取出基于该session存储的变量。（用户的登陆信息，登录名）
         */
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        //5.返回脱敏信息
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if(originUser==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"原始数据为空");
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 用户要拥有的标签
     * @param tagNameList
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1.先查询所有用户，存在内存中进行查找
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();

        //2.在内存中判断是否包含要求的标签
        //把下方的stream流改成parallelStream并行流就是并发
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            //需要判断取出的用户是否有tag值，不进行判断会出现java.lang.NullPointerException（报错）
            if(StringUtils.isBlank(tagsStr)){
                return false;
            }
            Set<String> tempTagNameSet = gson.fromJson(tagsStr,new TypeToken<Set<String>>(){}.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
//            //反序列化
//            gson.toJson(tempTagNameList);
            for (String tagName : tagNameList) {
                if(!tempTagNameSet.contains(tagName)){
                    return false;   //会被过滤掉
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public int updateUser(User user,User loginUser) {
        if(loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前用户数据为空");
        }
        if (user==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"当前用户数据为空");
        }
        long userId = user.getId();
        if(userId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!isAdmin(loginUser) && user.getId() != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    /**
     * 获取当前用户对象
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userObj==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return (User) userObj;
    }

    /**
     * 权限判断方法
     * @param request
     * @return false代表是普通用户  true代表为管理员
     */
    @Override
    public boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User)userObj;
        if(user.getUserRole() != ADMIN_ROLE || user == null) return false;
        return true;
    }

    /**
     * 权限判断方法
     * @param loginUser
     * @return false代表是普通用户  true代表为管理员
     */
    @Override
    public boolean isAdmin(User loginUser){
        if(loginUser.getUserRole() != ADMIN_ROLE || loginUser == null) return false;
        return true;
    }

    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(user -> getSafetyUser(user))
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }

    /**
     * 根据标签从数据库查询
     * @param tagNameList
     * @Deprecated 表示过期的注解
     * 定义成private防止外部调用
     * @return
     */
    @Deprecated
    private List<User> searchUserByTagsBySQL(List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //拼接and查询(iter快速生成)
        //like ‘%Java%’ and '%Python%'
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags",tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

}




