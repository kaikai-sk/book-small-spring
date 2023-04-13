package com.springframework.beans.factory;

public interface InitializingBean {
    /**
     * 在Bean对象完成属性填充后调用
     */
    void afterPropertiesSet() throws Exception;
}
