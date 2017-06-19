package com.cyparty.laihui.db;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by pangzhenpeng on 2017/6/17.
 */
public class ApiDB {
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplateObject;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
    }

    public boolean update(String table, String where) {
        boolean isSuccess = false;
        String SQL = "UPDATE " + table + where;
        int count = jdbcTemplateObject.update(SQL);
        if (count == 1) {
            isSuccess = true;
        }
        return isSuccess;
    }
}
