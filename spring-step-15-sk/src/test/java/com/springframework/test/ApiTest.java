package com.springframework.test;

import com.springframework.context.support.ClassPathXmlApplicationContext;
import com.springframework.test.bean.IUserService;
import org.junit.Test;

public class ApiTest {
    @Test
    public void test_autoProxy() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("测试结果：" + userService.queryUserInfo());
    }
}
