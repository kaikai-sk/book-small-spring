package com.springframework.beans.factory;

import com.springframework.beans.BeansException;
import com.springframework.context.ApplicationContext;

/**
 * 实现此接口，会感知到自己所属的ApplicationContext
 */
public interface ApplicationContextAware extends Aware {
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}
