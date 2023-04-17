package com.springframework.test;

import com.springframework.beans.BeansException;
import com.springframework.beans.PropertyValue;
import com.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.config.api.BeanFactoryPostProcessor;
import com.springframework.beans.PropertyValues;
import com.springframework.beans.BeansException;
import com.springframework.beans.factory.support.DefaultListableBeanFactory;
import com.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import com.springframework.context.support.ClassPathXmlApplicationContext;
import com.springframework.test.bean.UserService;
import com.springframework.test.common.MyBeanFactoryPostProcessor;
import com.springframework.test.common.MyBeanPostProcessor;
import org.junit.Test;

public class ApiTest {

    @Test
    public void test_xml() {
        // 1.初始化 BeanFactory
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.registerShutdownHook();

        // 2. 获取Bean对象调用方法
        UserService userService = applicationContext.getBean("userService", UserService.class);
        String result = userService.queryUserInfo();
        System.out.println("测试结果：" + result);
        System.out.println(String.format("ApplicatinoContextAware: %s", userService.getApplicationContext()));
        System.out.println(String.format("BeanFactoryAware: %s", userService.getBeanFactory()));
    }

    @Test
    public void test_hook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("close！")));
    }

}
