package com.springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.springframework.beans.BeansException;
import com.springframework.beans.PropertyValue;
import com.springframework.beans.PropertyValues;
import com.springframework.beans.factory.*;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.config.BeanReference;
import com.springframework.beans.factory.config.api.AutowireCapableBeanFactory;
import com.springframework.beans.factory.config.api.BeanPostProcessor;
import com.springframework.beans.factory.support.api.InstantiationStrategy;
import com.springframework.beans.factory.support.createbean.CglibSubClassingInstantiationStrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {
    private InstantiationStrategy instantiationStrategy = new CglibSubClassingInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        Object bean = null;
        try {
            bean = createBeanInstance(beanDefinition, beanName, args);
            // 为bean对象填充属性。
            applyPropertyValues(beanName, bean, beanDefinition);
            // 执行bean对象的初始化方法和BeanPostProcessor中的前置和后置处理
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception ex) {
            throw new BeansException("Instantiation of bean failed.", ex);
        }

        // 注册实现了DisposableBean接口的bean对象
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        // 只有单例模式的对象才会注册到容器中。
        if (beanDefinition.isSingleton()) {
            registerSingleton(beanName, bean);
        }
        return bean;
    }

    private void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 如果不是单例对象，不用注册销毁Adapter了。
        if (!beanDefinition.isSingleton()) {
            return;
        }
        if (bean instanceof DisposableBean || StrUtil.isNotEmpty(beanDefinition.getDestroyMethodName())) {
            registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition.getDestroyMethodName()));
        }
    }

    private Object initializeBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 0. 调用所有的Aware对象的感知方法
        if (bean instanceof Aware) {
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(this);
            }
            if (bean instanceof BeanClassLoaderAware) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
            }
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
        }

        // 1. 执行BeanPostProcessor中的前置处理。
        Object wrappenBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        if (wrappenBean == null) {
            wrappenBean = bean;
        }

        // 2. 调用对象的初始化方法
        try {
            invokeInitMethods(beanName, wrappenBean, beanDefinition);
        } catch (Exception e) {
            throw new BeansException(String.format("Invocation of init method of bean [%s] failed", beanName), e);
        }


        // 3. 执行BeanPostProcessor中的后置处理。
        wrappenBean = applyBeanPostProcessorsAfterInitialization(wrappenBean, beanName);
        if (wrappenBean == null) {
            wrappenBean = bean;
        }
        return wrappenBean;
    }

    private void invokeInitMethods(String beanName, Object bean, BeanDefinition beanDefinition) throws Exception {
        // 1。 调用InitializingBean的实现方法
        if (bean instanceof InitializingBean) {
            ((InitializingBean) bean).afterPropertiesSet();
        }

        // 2. 配置信息initMethod
        String initMethodName = beanDefinition.getInitMethodName();
        if (StrUtil.isNotEmpty(initMethodName) && !(bean instanceof InitializingBean)) {
            Method initMethod = beanDefinition.getBeanClass().getMethod(initMethodName);
            if (initMethod == null) {
                throw new BeansException(String.format("Could not find an init method '%s' on bean withd name '%s'", initMethodName, beanName));
            }
            initMethod.invoke(bean);
        }
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object current = beanPostProcessor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object current = beanPostProcessor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    private void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            String name = propertyValue.getName();
            Object value = propertyValue.getValue();

            if(value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                value = getBean(beanReference.getBeanName());
            }

            BeanUtil.setFieldValue(bean, name, value);
        }
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition, String beanName, Object[] args) {
        Constructor constructor = null;
        Class<?> beanClass = beanDefinition.getBeanClass();
        Constructor<?>[] declaredConstructors = beanClass.getDeclaredConstructors();
        for (Constructor tempConstructor: declaredConstructors) {
            if (args != null && tempConstructor.getParameterTypes().length == args.length) {
                constructor = tempConstructor;
                break;
            }
        }
        return getInstantiationStrategy().instantiate(beanDefinition, beanName, constructor, args);
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }
}
