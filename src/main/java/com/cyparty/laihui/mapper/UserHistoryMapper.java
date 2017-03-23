package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.UserHistory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class UserHistoryMapper implements RowMapper<UserHistory> {

    public UserHistory mapRow(ResultSet resultSet, int i) throws SQLException {
        UserHistory history=new UserHistory();

        history.set_id(resultSet.getInt("_id"));
        history.setDriver_order_id(resultSet.getInt("driver_order_id"));
        history.setPassenger_order_id(resultSet.getInt("passenger_order_id"));
        history.setBooking_order_id(resultSet.getInt("booking_order_id"));
        history.setOrder_source(resultSet.getInt("order_source"));
        history.setMobile(resultSet.getString("user_mobile"));
        history.setOrder_type(resultSet.getInt("order_type"));

        return history;
    }
}
