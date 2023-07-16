package com.lfy.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lfy.usercenter.common.BaseResponse;
import com.lfy.usercenter.model.enums.ErrorCode;
import com.lfy.usercenter.exception.BusinessException;
import com.lfy.usercenter.model.domain.User;
import com.lfy.usercenter.model.request.UserLoginRequest;
import com.lfy.usercenter.model.request.UserRegisterRequest;
import com.lfy.usercenter.service.UserService;
import com.lfy.usercenter.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lfy.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 * 限制跨域 @CrossOrigin(origins = {" http://localhost:5173"})
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 用户注册
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();

        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"所有信息都不能为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping ("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest,HttpServletRequest request){
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"账户和密码都不能为空");
        }
        User result = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(result);
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping ("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 用户查询
     * @param username
     * @param request
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if(!userService.isAdmin(request)){
//            return new ArrayList<>();
            throw new BusinessException(ErrorCode.NO_AUTH,"非管理员");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> result = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(result);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"传入的标签列表为空");
        }
        List<User> userList = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(userList);
    }


    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(@RequestBody long id,HttpServletRequest request){
        if(!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"非管理员不具备该权限");
        }
        if(id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //得到现在的用户态所表示的用户
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User)userObj;
        if(currentUser == null) throw new BusinessException(ErrorCode.NULL_ERROR);
        //判断数据库是否存在该用户
        //todo 校验用户是否合法
        User user = userService.getById(currentUser.getId());
        //返回脱敏信息
        User result = userService.getSafetyUser(user);
        return ResultUtils.success(result);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){
        //1.校验参数是否为空
        if(user == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"没有传入用户数据");
        }
        //2.是否具备权限
        User loginUser = userService.getLoginUser(request);
        //3.触发更新
        int result = userService.updateUser(user,loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 用户推荐
     * @param request
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum,HttpServletRequest request){
        User user = userService.getLoginUser(request);
        //如果有缓存，直接读取缓存
        String redisKey = String.format("goose:user:recommend:%s", user.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Page<User> userPage =(Page<User>) valueOperations.get(redisKey);
        if(userPage!=null){
            return ResultUtils.success(userPage);
        }
        //无缓存，查找数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //分页查询
        userPage = userService.page(new Page<>(pageNum,pageSize),queryWrapper);
//      List<User> result = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        //redis写入,一定要设置过期时间
        try {
            valueOperations.set(redisKey,userPage,100000000, TimeUnit.MICROSECONDS);
        } catch (Exception e) {
            log.error("redis-key setting is error",e);
        }
        return ResultUtils.success(userPage);
    }

    /**
     * 获取最匹配的用户
     *
     * @param num
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num, user));
    }

}
