package com.springframework.beans.factory.config.api;

public interface SingletonBeanRegistry {
    /**
     * 向一级缓存中注册对象
     *
     * @param beanName bean name
     * @param singletonObject Object单例实例
     */
    void registerSingleton(String beanName, Object singletonObject);

    /**
     * 从一级缓存中获取已经创建好的对象
     *
     * @param beanName bean name
     * @return Object单例实例
     */
    Object getSingleton(String beanName);
}
