package com.lfy.usercenter.service;

import com.lfy.usercenter.mapper.UserMapper;
import com.lfy.usercenter.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class insertUsersTest {
    @Resource
    private UserService userService;

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    @Test
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakeLFY");
            user.setAvatarUrl("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2F36d83539-4c8a-41c9-83c0-08e6c6ea24f5%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1689404298&t=031863af31126bbc534bf496542797e4");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("123");
            user.setEmail("123");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("11111111");
            user.setTags("[]");
            userList.add(user);
        }
        userService.saveBatch(userList,1000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    private ExecutorService executorService = new ThreadPoolExecutor(60,1000,10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    /**
     * 并发批量插入用户
     */
//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    @Test
    public void doConcurencyInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        int batchSize = 5000;
        int j = 0;

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        //分组
        for (int i = 0; i < 20; i++) {
            //List是线程不安全的，要想使用应该使用Collections.synchronizedList对其包装
//            List<User> userList = Collections.synchronizedList(new ArrayList<>());
            List<User> userList = new ArrayList<>();
            while (true){
                j++;
                User user = new User();
                user.setUsername("假用户");
                user.setUserAccount("fakeLFY");
                user.setAvatarUrl("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2F36d83539-4c8a-41c9-83c0-08e6c6ea24f5%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1689404298&t=031863af31126bbc534bf496542797e4");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode("11111111");
                user.setTags("[]");
                userList.add(user);
                if(j%batchSize==0)
                    break;
            }
            //使用了一下的操作它就变成异步的了
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            },executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
