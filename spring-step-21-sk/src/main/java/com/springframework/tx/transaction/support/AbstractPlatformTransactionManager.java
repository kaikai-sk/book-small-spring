package com.springframework.tx.transaction.support;

import com.springframework.tx.transaction.PlatformTransactionManager;
import com.springframework.tx.transaction.TransactionDefinition;
import com.springframework.tx.transaction.TransactionException;
import com.springframework.tx.transaction.TransactionStatus;

import java.io.Serializable;

public abstract class AbstractPlatformTransactionManager implements PlatformTransactionManager, Serializable {
    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        Object transaction = doGetTransaction();
        if (null == definition) {
            definition = new DefaultTransactionDefinition();
        }
        if (definition.getTimeout() < TransactionDefinition.TIMEOUT_DEFAULT) {
            throw new TransactionException("Invalid transaction timeout " + definition.getTimeout());
        }

        // 暂定默认的事务传播行为
        DefaultTransactionStatus status = newTransactionStatus(definition, transaction, true);
        // 开始事务
        doBegin(transaction, definition);
        return status;
    }

    protected DefaultTransactionStatus newTransactionStatus(TransactionDefinition definition, Object transaction, boolean newTransaction) {
        return new DefaultTransactionStatus(transaction, newTransaction);
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        if (status.isCompleted()) {
            throw new IllegalArgumentException(
                "Transaction is already completed - do not call commit or rollback more than once per transaction");
        }
        DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
        processRollback(defStatus, false);
    }

    private void processRollback(DefaultTransactionStatus status, boolean unexpected) {
        doRollback(status);
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        if (status.isCompleted()) {
            throw new IllegalArgumentException(
                "Transaction is already completed - do not call or rollback more than once per transaction");
        }
        DefaultTransactionStatus defStatus = (DefaultTransactionStatus) status;
        processCommit(defStatus);
    }

    private void processCommit(DefaultTransactionStatus status) throws TransactionException {
        doCommit(status);
    }

    /**
     * 获取事务
     *
     * @return
     * @throws TransactionException
     */
    protected abstract Object doGetTransaction() throws TransactionException;

    /**
     * 提交事务
     *
     * @param status
     * @throws TransactionException
     */
    protected abstract void doCommit(DefaultTransactionStatus status) throws TransactionException;

    /**
     * 回滚事务
     *
     * @param status
     * @throws TransactionException
     */
    protected abstract void doRollback(DefaultTransactionStatus status) throws TransactionException;

    protected abstract void doBegin(Object transaction, TransactionDefinition definition) throws TransactionException;
}
