package com.springframework.test.common;

import com.springframework.beans.BeansException;
import com.springframework.beans.PropertyValue;
import com.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.config.api.BeanFactoryPostProcessor;
import com.springframework.beans.PropertyValues;
import com.springframework.beans.BeansException;
/**
 *
 *
 *
 * 作者：DerekYRC https://github.com/DerekYRC/mini-spring
 * @description BeanFactoryPostProcessor 实例化 Bean 对象之前，修改 BeanDefinition 属性
 * @date 2022/03/10
 *
 *
 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("userService");
        PropertyValues propertyValues = beanDefinition.getPropertyValues();

        propertyValues.addPropertyValue(new PropertyValue("company", "改为：字节跳动"));
    }

}
