package com.springframework.beans.factory.support;

import cn.hutool.core.util.StrUtil;
import com.springframework.beans.BeansException;
import com.springframework.beans.factory.DisposableBean;

import java.lang.reflect.Method;

public class DisposableBeanAdapter implements DisposableBean {
    private final Object bean;

    private final String beanName;

    private String destroyMethodName;

    public DisposableBeanAdapter(Object bean, String beanName, String destroyMethodName) {
        this.bean = bean;
        this.beanName = beanName;
        this.destroyMethodName = destroyMethodName;
    }

    @Override
    public void destroy() throws Exception {
        // 1. 如果实现了DisposableBean接口
        if (bean instanceof DisposableBean) {
            ((DisposableBean) bean).destroy();
        }

        // 2. 配置信息destroy-method
        if (StrUtil.isNotEmpty(destroyMethodName) && !(bean instanceof DisposableBean)) {
            Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
            if (destroyMethod == null) {
                throw new BeansException(String.format("Could not find a destroy method named '%s' on bean with "
                    + "name '%s'", destroyMethod, beanName));
            }
            destroyMethod.invoke(bean);
        }
    }
}
