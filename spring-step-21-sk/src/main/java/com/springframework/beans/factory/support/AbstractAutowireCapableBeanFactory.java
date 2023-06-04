package com.springframework.beans.factory.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.springframework.aop.framework.JdkDynamicAopProxy;
import com.springframework.beans.BeansException;
import com.springframework.beans.PropertyValue;
import com.springframework.beans.PropertyValues;
import com.springframework.beans.factory.*;
import com.springframework.beans.factory.config.BeanDefinition;
import com.springframework.beans.factory.config.BeanReference;
import com.springframework.beans.factory.config.api.AutowireCapableBeanFactory;
import com.springframework.beans.factory.config.api.BeanPostProcessor;
import com.springframework.beans.factory.config.api.InstantiationAwareBeanPostProcessor;
import com.springframework.beans.factory.support.api.InstantiationStrategy;
import com.springframework.beans.factory.support.createbean.CglibSubClassingInstantiationStrategy;
import com.springframework.beans.factory.support.createbean.SimpleInstantiationStrategy;
import com.springframework.core.convert.ConversionService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {
    private InstantiationStrategy instantiationStrategy = new SimpleInstantiationStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition, Object[] args) throws BeansException {
        // 判断是否返回代理 Bean 对象
        Object bean = resolveBeforeInstantiation(beanName, beanDefinition);
        if (null != bean) {
            return bean;
        }

        return doCreateBean(beanName, beanDefinition, args);
    }

    protected Object doCreateBean(String beanName, BeanDefinition beanDefinition, Object[] args) {
        Object bean = null;
        try {
            bean = createBeanInstance(beanDefinition, beanName, args);

            // 处理循环依赖，将实例化之后的bean对象提前存储到缓存中。
            if(beanDefinition.isSingleton()) {
                Object finalBean = bean;
                addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, beanDefinition, finalBean));
            }

            // 实例化之后的判断
            boolean continueWithPropertyPopulation = applyBeanPostProcessorsAfterInstantiation(beanName, bean);
            if (!continueWithPropertyPopulation) {
                return bean;
            }

            // 在设置bean对象的属性之前，允许BeanPostProcessor接口修改属性值。
            applyBeanPostProcessorsBeforeApplyingPropertyValues(beanName, bean, beanDefinition);
            // 为bean对象填充属性。
            applyPropertyValues(beanName, bean, beanDefinition);
            // 执行bean对象的初始化方法和BeanPostProcessor中的前置和后置处理
            bean = initializeBean(beanName, bean, beanDefinition);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BeansException("Instantiation of bean failed.", ex);
        }

        // 注册实现了DisposableBean接口的bean对象
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

        // 只有单例模式的对象才会注册到容器中。
		Object exposedObject = bean;
        if (beanDefinition.isSingleton()) {
			// 获取代理对象
            exposedObject = getSingleton(beanName);
            registerSingleton(beanName, exposedObject);
        }
        return exposedObject;
    }

    protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
        Object exposedObject = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                exposedObject = ((InstantiationAwareBeanPostProcessor) beanPostProcessor)
                    .getEarlyBeanReference(exposedObject, beanName);
                if (exposedObject == null) {
                    return exposedObject;
                }
            }
        }

        return exposedObject;
    }

    protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) {
        Object bean = applyBeanPostProcessorsBeforeInstantiation(beanDefinition.getBeanClass(), beanName);
        if (null != bean) {
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
        }
        return bean;
    }

    protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                Object result = ((InstantiationAwareBeanPostProcessor) beanPostProcessor)
                    .postProcessBeforeInstantiation(beanClass, beanName);
                if (null != result)
                    return result;
            }
        }
        return null;
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

    private void applyPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            String name = propertyValue.getName();
            Object value = propertyValue.getValue();

            if(value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                value = getBean(beanReference.getBeanName());
            } else { // 类型转换
//                Class<?> sourceType = value.getClass();
//                Class<?> targetType = (Class<?>) TypeUtil.getFieldType(bean.getClass(), name);
//
//                ConversionService conversionService = getConversionService();
//                if (conversionService != null) {
//                    if (conversionService.canConvert(sourceType, targetType)) {
//                        value = conversionService.convert(value, targetType);
//                    }
//                }
            }

            System.out.println(String.format("beanName = %s, value name = %s, value = %s", beanName, name, value));
            BeanUtil.setFieldValue(bean, name, value);
        }
    }

    @Override
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public ConversionService getConversionService() {
        return conversionService;
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

/**
     * Bean 实例化后对于返回 false 的对象，不在执行后续设置 Bean 对象属性的操作
     *
     * @param beanName
     * @param bean
     * @return
     */
    private boolean applyBeanPostProcessorsAfterInstantiation(String beanName, Object bean) {
        boolean continueWithPropertyPopulation = true;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                InstantiationAwareBeanPostProcessor instantiationAwareBeanPostProcessor =
                    (InstantiationAwareBeanPostProcessor) beanPostProcessor;
                if (!instantiationAwareBeanPostProcessor.postProcessAfterInstantiation(bean, beanName)) {
                    continueWithPropertyPopulation = false;
                    break;
                }
            }
        }
        return continueWithPropertyPopulation;
    }

    /**
     * 在设置 Bean 属性之前，允许 BeanPostProcessor 修改属性值
     *
     * @param beanName
     * @param bean
     * @param beanDefinition
     */
    protected void applyBeanPostProcessorsBeforeApplyingPropertyValues(String beanName, Object bean, BeanDefinition beanDefinition) {
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                PropertyValues pvs = ((InstantiationAwareBeanPostProcessor) beanPostProcessor)
                    .postProcessPropertyValues(beanDefinition.getPropertyValues(), bean, beanName);
                if (pvs != null) {
                    for (PropertyValue propertyValue : pvs.getPropertyValues()) {
                        beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
                    }
                }
            }
        }
    }

    public InstantiationStrategy getInstantiationStrategy() {
        return instantiationStrategy;
    }

    public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
        this.instantiationStrategy = instantiationStrategy;
    }

 @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (null == current) return result;
			result = current;
			        }
        return result;
	}
	
	 @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (null == current) return result;
            result = current;
        }
        return result;
    }
            


   
}
