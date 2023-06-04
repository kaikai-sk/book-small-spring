package com.springframework.tx.transaction.interceptor;

import com.springframework.tx.transaction.PlatformTransactionManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.Serializable;

public class TransactionInterceptor extends TransactionAspectSupport implements MethodInterceptor, Serializable {
    public TransactionInterceptor(PlatformTransactionManager ptm, TransactionAttributeSource transactionAttributeSource) {
        setTransactionManager(ptm);
        setTransactionAttributeSource(transactionAttributeSource);
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return invokeWithinTransaction(methodInvocation.getMethod(), methodInvocation.getThis().getClass(),
            methodInvocation::proceed);
    }
}
