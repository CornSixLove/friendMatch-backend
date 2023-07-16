package com.lfy.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.security.NoSuchAlgorithmException;


@SpringBootTest
class UserCenterApplicationTests {

    @Test
    void digest()throws NoSuchAlgorithmException {
        String newPassword = DigestUtils.md5DigestAsHex(("abcd"+"mypassword").getBytes());
        System.out.println(newPassword);
        //getBytes将字符串转化为byte数组，并且将编码格式变成UTF-8
        //byte[] digest = md5.digest("abcd".getBytes(StandardCharsets.UTF_8));
        //将byte数组转换成字符串
        //String s = new String(digest);
    }

    @Test
    void contextLoads() {
    }

}
