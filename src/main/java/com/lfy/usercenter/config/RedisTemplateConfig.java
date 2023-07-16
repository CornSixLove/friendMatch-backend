package com.lfy.usercenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisTemplateConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //即将 key 序列化为字符串类型。
        template.setKeySerializer(RedisSerializer.string());
        return template;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
//        配置 RedisConnectionFactory 实例
//        return new LettuceConnectionFactory();
//        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
//        connectionFactory.setHostName("120.79.167.186");
//        connectionFactory.setPort(6379);
//        connectionFactory.setDatabase(0);
//        return connectionFactory;
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("120.79.167.186", 6379);
        return new LettuceConnectionFactory(config);
    }
}
