package com.springframework.beans.factory;

import com.springframework.beans.BeansException;

public interface BeanFactory {
    Object getBean(String name) throws BeansException;
}
