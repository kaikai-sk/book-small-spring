package com.springframework.beans.factory;

import com.springframework.beans.BeansException;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.config.api.AutowireCapableBeanFactory;
import com.springframework.beans.factory.config.api.ConfigurableBeanFactory;

/**
 *
 *
 *
 * 作者：DerekYRC https://github.com/DerekYRC/mini-spring
 * @description Configuration interface to be implemented by most listable bean factories.
 * In addition to {@link ConfigurableBeanFactory}, it provides facilities to
 * analyze and modify bean definitions, and to pre-instantiate singletons.
 * @date 2022/3/9
 *  /CodeDesignTutorials
 *
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

    BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    void preInstantiateSingletons() throws BeansException;

}
