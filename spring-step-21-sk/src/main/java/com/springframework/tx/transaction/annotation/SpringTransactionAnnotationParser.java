package com.springframework.tx.transaction.annotation;

import com.springframework.core.annotation.AnnotatedElementUtils;
import com.springframework.core.annotation.AnnotationAttributes;
import com.springframework.tx.transaction.interceptor.RollbackRuleAttribute;
import com.springframework.tx.transaction.interceptor.RuleBasedTransactionAttribute;
import com.springframework.tx.transaction.interceptor.TransactionAttribute;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

public class SpringTransactionAnnotationParser implements TransactionAnnotationParser, Serializable {
    @Override
    public TransactionAttribute parseTransactionAnnotation(AnnotatedElement element) {
        AnnotationAttributes attributes = AnnotatedElementUtils.findMergedAnnotationAttributes(
            element, Transactional.class, false, false);
        if (attributes != null) {
            return parseTransactionAnnotation(attributes);
        } else {
            return null;
        }
    }

    protected TransactionAttribute parseTransactionAnnotation(AnnotationAttributes attributes) {
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();

        List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
        for (Class<?> rbRule : attributes.getClassArray("rollbackFor")) {
            rollbackRules.add(new RollbackRuleAttribute(rbRule));
        }

        rbta.setRollbackRules(rollbackRules);
        return rbta;
    }
}
