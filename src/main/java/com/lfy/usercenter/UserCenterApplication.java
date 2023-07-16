package com.lfy.usercenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling开启spring对定时任务的支持

@SpringBootApplication
@MapperScan("com.lfy.usercenter.mapper")
@EnableScheduling
public class UserCenterApplication {
    public static void main(String[] args) {

        SpringApplication.run(UserCenterApplication.class, args);

    }
}
