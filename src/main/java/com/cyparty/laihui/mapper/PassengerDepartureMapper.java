package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Order;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class PassengerDepartureMapper implements RowMapper<Order> {

    public Order mapRow(ResultSet resultSet, int i) throws SQLException {
        Order order=new Order();

        order.set_id(resultSet.getInt("a._id"));
        order.setOrder_id(resultSet.getInt("order_id"));
        order.setUser_id(resultSet.getInt("b.user_id"));
        order.setDriver_id(resultSet.getInt("a.user_id"));
        order.setOrder_status(resultSet.getInt("order_status"));
        order.setUpdate_time(Utils.checkTime(resultSet.getString("update_time")));
        order.setCreate_time(Utils.checkTime(resultSet.getString("create_time")));
        order.setRemark(Utils.checkNull(resultSet.getString("remark")));
        order.setTradeNo(Utils.checkNull(resultSet.getString("trade_no")));
        order.setBoarding_point(resultSet.getString("boarding_point"));
        order.setBreakout_point(resultSet.getString("breakout_point"));
        order.setBooking_seats(resultSet.getInt("booking_seats"));
        order.setPrice(resultSet.getDouble("price"));
        order.setDeparture_time(Utils.checkTime(resultSet.getString("departure_time")));
        order.setDescription(resultSet.getString("description"));
        order.setIsArrive(resultSet.getInt("is_arrive"));
        order.setSurchargeType(resultSet.getInt("surcharge_type"));
        order.setSurchargeMoney(resultSet.getDouble("surcharge_money"));

        return order;
    }
}
