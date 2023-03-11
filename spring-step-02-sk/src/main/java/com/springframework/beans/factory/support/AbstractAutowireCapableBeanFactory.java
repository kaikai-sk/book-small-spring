package com.springframework.beans.factory.support;

import com.springframework.beans.BeansException;
import com.springframework.beans.factory.config.BeanDefinition;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {
    @Override
    protected Object createBean(String name, BeanDefinition beanDefinition) throws BeansException {
        Object bean = null;
        try {
            bean = beanDefinition.getBeanClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new BeansException("Instantiation of bean failed.", ex);
        }
        registerSingleton(name, bean);
        return bean;
    }
}
