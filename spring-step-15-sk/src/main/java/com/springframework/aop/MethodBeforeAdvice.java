package com.springframework.aop;

import java.lang.reflect.Method;

public interface MethodBeforeAdvice extends BeforeAdvice {
    /**
     *
     * @param method
     * @param args
     * @param target
     */
    void before(Method method, Object[] args, Object target);
}
