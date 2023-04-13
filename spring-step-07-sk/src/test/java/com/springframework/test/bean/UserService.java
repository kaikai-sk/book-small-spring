package com.springframework.test.bean;

import com.springframework.beans.factory.DisposableBean;
import com.springframework.beans.factory.InitializingBean;

public class UserService implements InitializingBean, DisposableBean {
    private String userId;

    private String company;

    private String location;

    private UserDao userDao;

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

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
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
}
