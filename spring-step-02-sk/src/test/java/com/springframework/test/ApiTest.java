package com.springframework.test;

import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.test.bean.UserService;
import org.junit.Test;

public class ApiTest {
    @Test
    public void testBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        beanFactory.registerBeanDefinition("userService", new BeanDefinition(UserService.class));

        UserService userService = (UserService) beanFactory.getBean("userService");
        userService.queryUserInfo();

        userService = (UserService) beanFactory.getBean("userService");
        userService.queryUserInfo();
    }
}
