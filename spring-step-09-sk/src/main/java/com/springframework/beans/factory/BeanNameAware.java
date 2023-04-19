package com.springframework.beans.factory;

/**
 * 实现此接口，会感知到自己的Bean对象的名称
 */
public interface BeanNameAware extends Aware {
    void setBeanName(String name);
}
