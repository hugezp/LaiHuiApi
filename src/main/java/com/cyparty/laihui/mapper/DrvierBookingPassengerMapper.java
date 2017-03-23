package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.DriverBookingPassenger;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class DrvierBookingPassengerMapper implements RowMapper<DriverBookingPassenger> {

    public DriverBookingPassenger mapRow(ResultSet resultSet, int i) throws SQLException {
        DriverBookingPassenger dbp=new DriverBookingPassenger();

        dbp.set_id(resultSet.getInt("_id"));
        dbp.setDriver_id(resultSet.getInt("driver_id"));
        dbp.setDriver_order_id(resultSet.getInt("driver_order_id"));
        dbp.setPassenger_order_id(resultSet.getInt("passenger_order_id"));
        dbp.setStatus(resultSet.getInt("status"));
        dbp.setCreate_time(resultSet.getString("create_time"));
        dbp.setBooking_seats(resultSet.getInt("booking_seats"));


        return dbp;
    }
}
