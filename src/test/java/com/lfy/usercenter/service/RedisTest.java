package com.lfy.usercenter.service;
import java.util.Date;

import com.lfy.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("lfyString","lifuyu");
        valueOperations.set("lfyInt",1);
        valueOperations.set("lfydouble",2.0);
        User user = new User();
        user.setId(0L);
        user.setUsername("lfy");
        user.setUserAccount("lifuyu");
        valueOperations.set("lfyUser",user);

        Object lfyInt = valueOperations.get("lfyString");
        Assertions.assertTrue("lifuyu".equals((String) lfyInt));
        lfyInt = valueOperations.get("lfyInt");
        Assertions.assertTrue(1==(Integer) lfyInt);
        lfyInt = valueOperations.get("lfydouble");
        Assertions.assertTrue(2.0==(Double) lfyInt);
        System.out.println(lfyInt = valueOperations.get("lfyUser"));

    }
}
