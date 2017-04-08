package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.TestController;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/4/7.
 */
public class TestControllerMapper implements RowMapper<TestController> {
    @Override
    public TestController mapRow(ResultSet resultSet, int i) throws SQLException {
        TestController history=new TestController();
        history.set_id(resultSet.getInt("_id"));
        history.setController(resultSet.getString("controller"));
        history.setLogin_ip(resultSet.getString("login_ip"));
        history.setCreate_time(resultSet.getString("create_time"));
        history.setMy_mobile(resultSet.getString("my_mobile"));
        history.setMobile(resultSet.getString("mobile"));
        return history;
    }
}
