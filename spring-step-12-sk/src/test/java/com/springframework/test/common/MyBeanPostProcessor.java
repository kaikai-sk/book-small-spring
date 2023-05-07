package com.springframework.test.common;

import com.springframework.beans.BeansException;
import com.springframework.beans.factory.config.api.BeanFactoryPostProcessor;
import com.springframework.beans.factory.config.api.BeanPostProcessor;
import com.springframework.test.bean.UserService;

/**
 *
 *
 *
 * 作者：DerekYRC https://github.com/DerekYRC/mini-spring
 * @description BeanPostProcessor 在 Bean 对象执行初始化方法前后进行扩展
 * @date 2022/03/10
 *
 *
 */
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("userService".equals(beanName)) {
            UserService userService = (UserService) bean;
            userService.setLocation("改为：北京");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
