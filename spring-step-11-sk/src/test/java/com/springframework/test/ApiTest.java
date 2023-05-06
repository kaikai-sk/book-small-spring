package com.springframework.test;

import com.springframework.aop.MethodMatcher;
import com.springframework.aop.aspectj.AspectJExpressionPointcut;
import com.springframework.aop.framework.ReflectiveMethodInvocation;
import com.springframework.test.bean.IUserDao;
import com.springframework.test.bean.IUserService;
import com.springframework.test.bean.UserService;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Test;

import javax.xml.transform.Source;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ApiTest {
    @Test
    public void test_aop() throws NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut(
            "execution(* com.springframework.test.bean.UserService.*(..))");
        Class<UserService> clazz = UserService.class;
        Method method = clazz.getDeclaredMethod("queryUserInfo");

        System.out.println(pointcut.matches(clazz));
        System.out.println(pointcut.matches(method, clazz));
    }

    @Test
    public void test_proxy_method() {
        // 目标对象（可以是任何的目标对象）
        Object targetObject = new UserService();

        IUserService proxy = (IUserService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
            targetObject.getClass().getInterfaces(),
            new InvocationHandler() {
            MethodMatcher methodMatcher = new AspectJExpressionPointcut("execution(* com.springframework.test.bean.IUserService.*(..))");
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if(methodMatcher.matches(method, targetObject.getClass())) {
                        MethodInterceptor methodInterceptor = invocation -> {
                            long start = System.currentTimeMillis();
                            try {
                                return invocation.proceed();
                            } finally {
                                System.out.println("监控 - begin by aop");
                                System.out.println("方法名称：" + invocation.getMethod().getName());
                                System.out.println("方法耗时：" + (System.currentTimeMillis() - start) + "ms");
                                System.out.println("监控 end \r\n");
                            }
                        };
                        return methodInterceptor.invoke(new ReflectiveMethodInvocation(targetObject, method, args));
                    }
                    return method.invoke(targetObject, args);
                }
            });

        String result = proxy.queryUserInfo();
        System.out.println(result);
    }

    @Test
    public void test_proxy_class() {
        IUserService userService = (IUserService) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
            new Class[]{IUserService.class},
            (proxy, method, args) -> "你被代理了！ ");

        String result = userService.queryUserInfo();
        System.out.println(result);
    }
}
