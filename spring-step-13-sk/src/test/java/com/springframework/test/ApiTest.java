package com.springframework.test;

import com.springframework.aop.AdvisedSupport;
import com.springframework.aop.ClassFilter;
import com.springframework.aop.TargetSource;
import com.springframework.aop.aspectj.AspectJExpressionPointcut;
import com.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import com.springframework.aop.framework.ProxyFactory;
import com.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import com.springframework.context.support.ClassPathXmlApplicationContext;
import com.springframework.test.bean.IUserService;
import com.springframework.test.bean.UserService;
import com.springframework.test.bean.UserServiceBeforeAdvice;
import com.springframework.test.bean.UserServiceInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Before;
import org.junit.Test;

public class ApiTest {
    @Test
    public void test_scan() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-scan.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("测试结果：" + userService.queryUserInfo());
    }

    @Test
    public void test_property() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-property.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("测试结果：" + userService);
    }
}
