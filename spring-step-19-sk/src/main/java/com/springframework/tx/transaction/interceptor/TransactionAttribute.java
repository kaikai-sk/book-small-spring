package com.springframework.tx.transaction.interceptor;

import com.springframework.tx.transaction.TransactionDefinition;

/**
 *
 * @description This interface adds a {@code rollbackOn} specification to {@link TransactionDefinition}.
 * @date 2022/3/16
 *  /CodeDesignTutorials
 *
 */
public interface TransactionAttribute extends TransactionDefinition {
    boolean rollbackOn(Throwable ex);
}
