package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.PassengerOrder;
import com.cyparty.laihui.domain.PassengerPublishInfo;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class PassengerInfoMapper implements RowMapper<PassengerPublishInfo> {

    public PassengerPublishInfo mapRow(ResultSet resultSet, int i) throws SQLException {
        PassengerPublishInfo order=new PassengerPublishInfo();

        order.set_id(resultSet.getInt("_id"));
        order.setUser_id(resultSet.getInt("user_id"));
        order.setPrice(resultSet.getDouble("price"));
        order.setMobile(resultSet.getString("mobile"));
        order.setBoarding_point(Utils.checkNull(resultSet.getString("boarding_point")));
        order.setBreakout_point(Utils.checkNull(resultSet.getString("breakout_point")));
        order.setDescription(Utils.checkNull(resultSet.getString("description")));
        order.setCreate_time(resultSet.getString("create_time").split("\\.")[0]);
        return order;
    }
}
