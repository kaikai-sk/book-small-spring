package com.springframework.beans.factory;

import com.springframework.beans.BeansException;

/**
 * 实现此接口，会感知到自己所属的BeanFactory
 */
public interface BeanFactoryAware extends Aware {
    void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
