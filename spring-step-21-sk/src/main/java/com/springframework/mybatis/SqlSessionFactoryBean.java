package com.springframework.mybatis;

import com.middleware.mybatis.SqlSessionFactory;
import com.middleware.mybatis.SqlSessionFactoryBuilder;
import com.springframework.beans.factory.FactoryBean;
import com.springframework.beans.factory.InitializingBean;
import com.springframework.core.io.DefaultResourceLoader;
import com.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

public class SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>, InitializingBean {
    private String resource;
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Resource resource1 = defaultResourceLoader.getResource(this.resource);

        try (InputStream inputStream = resource1.getInputStream()){
            this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public SqlSessionFactory getObject() throws Exception {
        return this.sqlSessionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return SqlSessionFactory.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
