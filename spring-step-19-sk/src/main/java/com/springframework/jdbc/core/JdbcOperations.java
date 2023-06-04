package com.springframework.jdbc.core;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public interface JdbcOperations {
    <T> T execute(StatementCallback<T> action);

    void execute(String sql);

    List<Map<String, Object>> queryForList(String sql);

    <T> T query(String sql, ResultSetExtractor<T> res);

    <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse);

    <T> List<T> query(String sql, RowMapper<T> rowMapper);

    <T> T query(String sql, PreparedStatement pss, ResultSetExtractor<T> rse);
}
