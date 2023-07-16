package com.lfy.usercenter.job.work;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lfy.usercenter.model.domain.User;
import com.lfy.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 */
@Slf4j
@Component
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    /**
     *  @Scheduled(cron = "0 0 12 * * ?") 是一个定时任务
     * 每天执行，加载预热账户
     * crontab表达式可以去网上生成
     */
    @Scheduled(cron = "0 0 12 * * ?")
    synchronized public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("UserCenter:PreCacheJob:doCacheRecommendUser:lock");
        try {
            //-1代表redisson开启看门狗机制，默认过期时间为30s，然后每10s会检查一次
            if(lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                System.out.println("getLock: " + Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    //无缓存，查找数据库
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    //分页查询
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("goose:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    //redis写入,一定要设置过期时间
                    try {
                        valueOperations.set(redisKey,userPage,10000000, TimeUnit.MICROSECONDS);
                    } catch (Exception e) {
                        log.error("redis-key setting is error",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error",e);
        } finally {
            //判断锁的持有者，只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
