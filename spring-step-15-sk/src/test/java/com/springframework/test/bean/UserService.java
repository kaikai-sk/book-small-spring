package com.springframework.test.bean;

import com.springframework.beans.factory.annotation.Autowired;
import com.springframework.beans.factory.annotation.Value;
import com.springframework.context.annotation.Component;

import java.util.Random;

public class UserService implements IUserService {
    private String token;

    public UserService() {
    }

    @Override
    public String queryUserInfo() {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "小傅哥，100001，深圳，" + token;
    }

    @Override
    public String register(String userName) {
        try {
            Thread.sleep(new Random(1).nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "注册用户：" + userName + " success！";
    }

    @Override
    public String toString() {
        return String.format("UserService#token = { %s }", token);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
