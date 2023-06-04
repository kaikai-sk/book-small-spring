package com.springframework.test;

import com.springframework.aop.AdvisedSupport;
import com.springframework.aop.TargetSource;
import com.springframework.aop.aspectj.AspectJExpressionPointcut;
import com.springframework.aop.framework.Cglib2AopProxy;
import com.springframework.context.support.ClassPathXmlApplicationContext;
import com.springframework.jdbc.core.JdbcTemplate;
import com.springframework.jdbc.datasource.DataSourceTransactionManager;
import com.springframework.test.bean.JdbcService;
import com.springframework.tx.transaction.annotation.AnnotationTransactionAttributeSource;
import com.springframework.tx.transaction.interceptor.TransactionInterceptor;
import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 *
 * @description
 * @date 2022/3/16
 *  /CodeDesignTutorials
 *
 */
public class ApiTest {
    private JdbcTemplate jdbcTemplate;
    private JdbcService jdbcService;
    private DataSource dataSource;

    @Before
    public void init() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
        dataSource = applicationContext.getBean(DruidDataSource.class);
        jdbcService = applicationContext.getBean(JdbcService.class);
    }

    @Test
    public void test_Transaction() throws SQLException {
        AnnotationTransactionAttributeSource transactionAttributeSource = new AnnotationTransactionAttributeSource();
        transactionAttributeSource.findTransactionAttribute(jdbcService.getClass());

        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        TransactionInterceptor interceptor = new TransactionInterceptor(transactionManager, transactionAttributeSource);

        // 组装代理信息
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTargetSource(new TargetSource(jdbcService));
        advisedSupport.setMethodInterceptor(interceptor);
        advisedSupport.setMethodMatcher(new AspectJExpressionPointcut("execution(* com.springframework.test.bean.JdbcService.*(..))"));

        // 代理对象(Cglib2AopProxy)
        JdbcService proxy_cglib = (JdbcService) new Cglib2AopProxy(advisedSupport).getProxy();

        // 测试调用，有事务【不能同时提交2条有主键冲突的数据】
        proxy_cglib.saveData(jdbcTemplate);

        // 测试调用，无事务【提交2条有主键冲突的数据成功一条】
        // proxy_cglib.saveDataNoTransaction(jdbcTemplate);
    }

}
