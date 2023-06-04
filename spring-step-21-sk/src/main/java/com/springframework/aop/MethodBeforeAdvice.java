package com.springframework.aop;

import java.lang.reflect.Method;

/**
 *
 *
 *
 * ×÷Õß£ºDerekYRC https://github.com/DerekYRC/mini-spring
 * @description Advice invoked before a method is invoked. Such advices cannot
 * prevent the method call proceeding, unless they throw a Throwable.
 * @date 2022/3/14
 *  /CodeDesignTutorials
 *
 */
public interface MethodBeforeAdvice extends BeforeAdvice{

    /**
     *
     * @param method
     * @param args
     * @param target
     */
    void before(Method method, Object[] args, Object target) throws Throwable;

}
