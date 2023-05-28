package com.springframework.beans.factory.config;

import com.springframework.beans.BeansException;
import com.springframework.beans.factory.config.api.BeanDefinitionReader;
import com.springframework.beans.factory.config.api.BeanDefinitionRegistry;
import com.springframework.core.io.DefaultResourceLoader;
import com.springframework.core.io.Resource;
import com.springframework.core.io.ResourceLoader;

public abstract class AbstractBeamDefinitionReader implements BeanDefinitionReader {
    private final BeanDefinitionRegistry registry;

    private final ResourceLoader resourceLoader;

    public AbstractBeamDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        this.registry = registry;
        this.resourceLoader = resourceLoader;
    }
    protected AbstractBeamDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, new DefaultResourceLoader());
    }


    @Override
    public BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }
}
