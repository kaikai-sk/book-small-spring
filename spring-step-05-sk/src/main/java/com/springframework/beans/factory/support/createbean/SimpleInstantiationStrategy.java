package com.springframework.beans.factory.support.createbean;

import com.springframework.beans.BeansException;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.support.api.InstantiationStrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SimpleInstantiationStrategy implements InstantiationStrategy {
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName, Constructor constructor, Object[] args)
        throws BeansException {
        Class clazz = beanDefinition.getBeanClass();
        try {
            if (constructor != null) {
                return clazz.getDeclaredConstructor(constructor.getParameterTypes()).newInstance(args);
            } else {
                return clazz.getDeclaredConstructor().newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new BeansException(String.format("Failed to instantiate [%s]", clazz.getName()), ex);
        }
    }
}
