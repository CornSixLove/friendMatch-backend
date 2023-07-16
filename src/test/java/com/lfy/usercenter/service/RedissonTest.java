package com.lfy.usercenter.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void redissonTest(){
        //list
        RList<String> list = redissonClient.getList("test-list");
        list.add("lfy");
        list.get(0);
        System.out.println("list" + " : "+list.get(0));
        list.remove(0);

        //map

    }
}
