package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.UserLoginIP;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/4/6.
 */
public class UserLoginIPMapper implements RowMapper<UserLoginIP> {
    @Override
    public UserLoginIP mapRow(ResultSet resultSet, int i) throws SQLException {
        UserLoginIP userLoginIP = new UserLoginIP();
        userLoginIP.set_id(resultSet.getInt("_id"));
        userLoginIP.setLogin_time(resultSet.getString("login_time"));
        userLoginIP.setLogin_ip(resultSet.getString("login_ip"));
        userLoginIP.setMobile(resultSet.getString("mobile"));
        userLoginIP.setCode(resultSet.getString("code"));
        return null;
    }
}
