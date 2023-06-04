package com.springframework.jdbc.datasource;

import com.springframework.tx.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DataSourceUtils {
    public static Connection getConnection(DataSource dataSource) {
        try {
            return doGetConnection(dataSource);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to obtain JDBC Connection", e);
        }
    }

    /**
     * Actually obtain a JDBC Connection from the given DataSource.
     * Same as {@link #getConnection}, but throwing the original SQLException.
     * <p>Is aware of a corresponding Connection bound to the current thread, for example
     * when using {@link DataSourceTransactionManager}. Will bind a Connection to the thread
     * if transaction synchronization is active (e.g. if in a JTA transaction).
     * @param dataSource the DataSource to obtain Connections from
     * @return a JDBC Connection from the given DataSource
     * @throws SQLException if thrown by JDBC methods
     * @see #doReleaseConnection
     */
    public static Connection doGetConnection(DataSource dataSource) throws SQLException {
        ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
        if (null != conHolder && conHolder.hasConnection()) {
            return conHolder.getConnection();
        }
        return fetchConnection(dataSource);
    }

    private static Connection fetchConnection(DataSource dataSource) throws SQLException {
        Connection conn = dataSource.getConnection();
        if (null == conn) {
            throw new IllegalArgumentException("DataSource return null from getConnection():" + dataSource);
        }
        return conn;
    }
}
