package com.springframework.test.bean;

import com.springframework.beans.BeansException;
import com.springframework.beans.factory.*;
import com.springframework.context.ApplicationContext;
import com.springframework.context.ApplicationContextAware;

public class UserService implements InitializingBean, DisposableBean, BeanNameAware, BeanClassLoaderAware,
    BeanFactoryAware, ApplicationContextAware {
    private ApplicationContext applicationContext;

    private BeanFactory beanFactory;

    private String userId;

    private String company;

    private String location;

    private IUserDao userDao;

    public UserService() {
    }

    public String queryUserInfo() {
        return userDao.queryUserName(userId) + "," + company + "," + location;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public IUserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDao userDao) {
        this.userDao = userDao;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("execute: UserService.destroy");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("execute: UserService.afterPropertiesSet");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        System.out.println(String.format("ClassLoader: %s", classLoader));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanName(String name) {
        System.out.println(String.format("Bean name is : %s", name));
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
