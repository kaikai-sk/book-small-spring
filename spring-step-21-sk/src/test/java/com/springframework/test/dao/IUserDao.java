package com.springframework.test.dao;

import com.springframework.test.po.User;

public interface IUserDao {

     User queryUserInfoById(Long id);

}
