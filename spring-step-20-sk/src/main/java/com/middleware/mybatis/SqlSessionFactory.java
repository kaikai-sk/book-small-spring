package com.middleware.mybatis;

/**
 *
 * @description SqlSessionFactory
 * @date 2022/3/16
 *
 *
 */
public interface SqlSessionFactory {

    SqlSession openSession();

}
