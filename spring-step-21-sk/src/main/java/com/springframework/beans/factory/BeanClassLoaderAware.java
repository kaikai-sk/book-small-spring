package com.springframework.beans.factory;

import com.springframework.beans.BeansException;

/**
 * 实现此接口，会感知到自己所属的ClassLoader
 */
public interface BeanClassLoaderAware extends Aware {
    void setBeanClassLoader(ClassLoader classLoader);
}
