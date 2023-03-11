package com.springframework.beans.factory.support;

import com.springframework.beans.BeansException;
import com.springframework.beans.factory.BeanFactory;
import com.springframework.beans.factory.config.BeanDefinition;

/**
 * 模板类，定义主流程
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {
    @Override
    public Object getBean(String name) throws BeansException {
        Object bean = getSingleton(name);
        if (bean != null) {
            return bean;
        }

        BeanDefinition beanDefinition = getBeanDefinition(name);
        return createBean(name, beanDefinition);
    }



    protected abstract BeanDefinition getBeanDefinition(String name) throws BeansException;

    protected abstract Object createBean(String name, BeanDefinition beanDefinition) throws BeansException;
}
