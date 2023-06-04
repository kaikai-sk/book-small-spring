package com.springframework.mybatis;

import com.middleware.mybatis.SqlSessionFactory;
import com.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class MapperFactoryBean<T> implements FactoryBean<T> {
    private Class<?> mapperInterface;
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public T getObject() throws Exception {
        InvocationHandler handler = (proxy, method, args) -> {
            // 排除Object的方法：toString  hashCode
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }
            try {
                System.out.println("你被代理了， 执行SQL操作！ " + method.getName());
                return sqlSessionFactory.openSession().selectOne(mapperInterface.getName()
                    + "." + method.getName(), args[0]);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return method.getReturnType().newInstance();
        };
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
            new Class[]{mapperInterface}, handler);
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
