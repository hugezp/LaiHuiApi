package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Manager;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class ManagerMapper implements RowMapper<Manager> {

    public Manager mapRow(ResultSet resultSet, int i) throws SQLException {
        Manager manager=new Manager();

        manager.setM_id(resultSet.getInt("_id"));
        manager.setEnable(resultSet.getInt("is_enable"));
        manager.setName(resultSet.getString("m_name"));
        manager.setMobile(Utils.checkNull(resultSet.getString("mobile")));
        manager.setDecription(Utils.checkNull(resultSet.getString("description")));

        manager.setCreate_time(Utils.checkTime(resultSet.getString("create_time")));
        manager.setLast_login_time(Utils.checkTime(resultSet.getString("last_login_time")));
        manager.setPrivilege(resultSet.getInt("m_privilege"));

        return manager;
    }
}
