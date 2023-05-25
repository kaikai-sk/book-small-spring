package com.springframework.beans.factory.config.api;

import com.springframework.beans.BeansException;
import com.springframework.beans.factory.ConfigurableListableBeanFactory;

/**
 * 在所有的BeanDefinition加载完成后，将Bean对象实例化之前，提供修改BeanDefinition属性的能力。
 */
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
