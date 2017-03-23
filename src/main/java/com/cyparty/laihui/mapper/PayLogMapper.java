package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.PayLog;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class PayLogMapper implements RowMapper<PayLog> {

    public PayLog mapRow(ResultSet resultSet, int i) throws SQLException {
        PayLog pay=new PayLog();
        pay.set_id(resultSet.getInt("_id"));
        pay.setPay_type(resultSet.getInt("pay_type"));
        pay.setIs_complete(resultSet.getInt("is_complete"));
        pay.setOrder_id(resultSet.getInt("order_id"));
        pay.setP_id(resultSet.getInt("p_id"));
        pay.setDriver_id(resultSet.getInt("driver_id"));
        pay.setCash(resultSet.getDouble("cash"));

        pay.setDeparture_time(Utils.checkTime(resultSet.getString("departure_time")));
        pay.setOrder_status(resultSet.getInt("order_status"));
        return pay;
    }
}
