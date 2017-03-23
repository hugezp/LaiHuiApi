package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.DriverPayAccount;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class DriverPayAccountMapper implements RowMapper<DriverPayAccount> {

    public DriverPayAccount mapRow(ResultSet resultSet, int i) throws SQLException {
        DriverPayAccount payAccount=new DriverPayAccount();

        payAccount.setId(resultSet.getInt("_id"));
        payAccount.setUser_id(resultSet.getInt("user_id"));
        payAccount.setPay_account(resultSet.getString("pay_account"));
        payAccount.setCreate_time(resultSet.getString("create_time").split(" ")[0]);
        payAccount.setLast_updated(resultSet.getString("last_updated_time").split(" ")[0]);

        return payAccount;
    }
}
