package com.lfy.usercenter.service;

import com.lfy.usercenter.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 * 用户服务测试
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

//    @Test
//    void userRegister() {
//        String userAccount = "lfy";
//        String userPassword = "123456";
//        String checkPassword = "123456";
//        String planetCode = 1
//        long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
//        Assertions.assertEquals(-1,result);
//
//        userAccount = "lifuyu";
//        userPassword = "asd1315547555";
//        checkPassword = "asd1315547555";
//        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
//        Assertions.assertTrue(result>0);
//    }


    /**
     * 测试类是不加参数的
     */
    @Test
    public void testsearchUserByTags(){
        List<String> list = Arrays.asList("java","python");
        List<User> userList = userService.searchUserByTags(list);
        Assert.assertNotNull(userList);
    }
}