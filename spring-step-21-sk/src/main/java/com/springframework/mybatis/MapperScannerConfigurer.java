package com.springframework.mybatis;

import cn.hutool.core.lang.ClassScanner;
import com.middleware.mybatis.SqlSessionFactory;
import com.springframework.beans.BeansException;
import com.springframework.beans.PropertyValue;
import com.springframework.beans.PropertyValues;
import com.springframework.beans.factory.ConfigurableListableBeanFactory;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.config.api.BeanDefinitionRegistry;
import com.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.awt.*;
import java.util.Set;

public class MapperScannerConfigurer implements BeanDefinitionRegistryPostProcessor {
    private String basePackage;

    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<Class<?>> classes = ClassScanner.scanPackage(basePackage);
        for (Class<?> clazz : classes) {
            PropertyValues propertyValues = new PropertyValues();
            propertyValues.addPropertyValue(new PropertyValue("mapperInterface", clazz));
            propertyValues.addPropertyValue(new PropertyValue("sqlSessionFactory", sqlSessionFactory));
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            beanDefinition.setPropertyValues(propertyValues);
            beanDefinition.setBeanClass(MapperFactoryBean.class);

            registry.registerBeanDefinition(clazz.getSimpleName(), beanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }
}
