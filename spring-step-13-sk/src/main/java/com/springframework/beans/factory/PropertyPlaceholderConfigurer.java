package com.springframework.beans.factory;

import com.springframework.beans.BeansException;
import com.springframework.beans.PropertyValue;
import com.springframework.beans.PropertyValues;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.config.api.BeanFactoryPostProcessor;
import com.springframework.core.io.DefaultResourceLoader;
import com.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

public class PropertyPlaceholderConfigurer implements BeanFactoryPostProcessor {
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    private String location;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(location);
            Properties properties = new Properties();
            properties.load(resource.getInputStream());

            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            for (String beanDefinitionName : beanDefinitionNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
                PropertyValues propertyValues = beanDefinition.getPropertyValues();
                for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
                    Object value = propertyValue.getValue();
                    if (!(value instanceof String)) {
                        continue;
                    }
                    String strValue = (String) value;
                    StringBuilder stringBuilder = new StringBuilder(strValue);
                    int startIdx = stringBuilder.indexOf(DEFAULT_PLACEHOLDER_PREFIX);
                    int stopIdx = stringBuilder.indexOf(DEFAULT_PLACEHOLDER_SUFFIX);
                    if (startIdx != -1 && stopIdx != -1 && startIdx < stopIdx) {
                        String propKey = strValue.substring(startIdx +2, stopIdx);
                        String propVal = properties.getProperty(propKey);
                        stringBuilder.replace(startIdx, stopIdx + 1, propVal);
                        propertyValues.addPropertyValue(new PropertyValue(propertyValue.getName(),
                            stringBuilder.toString()));
                    }
                }
            }
        } catch (IOException exception) {
            throw new BeansException("Could not load properties", exception);
        }
    }

    public void setLocation(String location) {
        this.location = location;
    }
}






















