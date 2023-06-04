package com.springframework.jdbc.core;

import cn.hutool.core.lang.Assert;
import com.springframework.jdbc.UncategorizedSQLException;
import com.springframework.jdbc.datasource.DataSourceUtils;
import com.springframework.jdbc.support.JdbcAccessor;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class JdbcTemplate extends JdbcAccessor implements JdbcOperations {
    /**
     * If this variable is set to a non-negative value, it will be used for setting the
     * fetchSize property on statements used for query processing.
     */
    private int fetchSize = -1;

    /**
     * If this variable is set to a non-negative value, it will be used for setting the
     * maxRows property on statements used for query processing.
     */
    private int maxRows = -1;

    private static <T> T result(T result) {
        Assert.state(null != result, "No result");
        return result;
    }

    @Override
    public <T> T query(String sql, ResultSetExtractor<T> res) {
        class QueryStatementCallback implements StatementCallback<T>, SqlProvider {

            @Override
            public String getSql() {
                return sql;
            }

            @Override
            public T doInStatement(Statement statement) throws SQLException {
                ResultSet rs = statement.executeQuery(sql);
                return res.extractData(rs);
            }
        }

        return execute(new QueryStatementCallback());
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql) {
        return query(sql, new ColumnMapRowMapper());
    }


    @Override
    public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) {
        return null;
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        return result(query(sql, new RowMapperResultSetExtractor<>(rowMapper)));
    }

    @Override
    public <T> T query(String sql, PreparedStatement pss, ResultSetExtractor<T> rse) {
        return null;
    }

    @Override
    public <T> T execute(StatementCallback<T> action) {
        Connection con = DataSourceUtils.getConnection(obtainDataSource());
        try {
            if (con.getAutoCommit()) {
                con.setAutoCommit(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            Statement stmt = con.createStatement();
            applyStatementSettings(stmt);
            return action.doInStatement(stmt);
        } catch (SQLException ex) {
            throw new UncategorizedSQLException("ConnectionCallback", getSql(action), ex);
        }
    }

    private static String getSql(Object sqlProvider) {
        if (sqlProvider instanceof SqlProvider) {
            return ((SqlProvider) sqlProvider).getSql();
        } else {
            return null;
        }
    }

    @Override
    public void execute(String sql) {
        class ExecuteStatementCallback implements StatementCallback<Object>, SqlProvider {
            @Override
            public Object doInStatement(Statement statement) throws SQLException {
                statement.execute(sql);
                return null;
            }
            @Override
            public String getSql() {
                return sql;
            }
        }
        execute(new ExecuteStatementCallback());
    }

    protected void applyStatementSettings(Statement stat) throws SQLException {
        int fetchSize = getFetchSize();
        if (fetchSize != -1) {
            stat.setFetchSize(fetchSize);
        }

        int maxRows = getMaxRows();
        if (maxRows != -1) {
            stat.setMaxRows(maxRows);
        }
    }

    public int getMaxRows() {
        return maxRows;
    }

    public int getFetchSize() {
        return fetchSize;
    }
}
