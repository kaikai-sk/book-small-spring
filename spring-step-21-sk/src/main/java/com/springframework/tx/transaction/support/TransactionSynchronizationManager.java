package com.springframework.tx.transaction.support;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.threadlocal.NamedThreadLocal;

import java.util.HashMap;
import java.util.Map;

public class TransactionSynchronizationManager {
    /**
     * 当前线程链接存储
     */
    private static final ThreadLocal<Map<Object, Object>> resources = new NamedThreadLocal<>("Transactional resources");

    /**
     * 当前事务名称
     */
    private static final ThreadLocal<String> currentTransactionName = new NamedThreadLocal<>("Current Transaction Name");

    public static void bindResource(Object key, Object value) throws IllegalStateException {
        Assert.notNull(value, "value must not be null");
        Map<Object, Object> map = resources.get();
        if (null == map) {
            map = new HashMap<>();
            resources.set(map);
        }

        map.put(key, value);
    }

    public static Object getResource(Object key) {
        return doGetResource(key);
    }

    private static Object doGetResource(Object actualKey) {
        Map<Object, Object> map = resources.get();
        if (null == map) {
            return null;
        }
        return map.get(actualKey);
    }
}
