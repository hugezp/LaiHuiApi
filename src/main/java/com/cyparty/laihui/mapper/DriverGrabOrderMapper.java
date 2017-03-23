package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.DriverGrabInfo;
import com.cyparty.laihui.domain.PassengerOrder;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class DriverGrabOrderMapper implements RowMapper<DriverGrabInfo> {

    public DriverGrabInfo mapRow(ResultSet resultSet, int i) throws SQLException {
        DriverGrabInfo grabInfo=new DriverGrabInfo();

        grabInfo.set_id(resultSet.getInt("_id"));
        grabInfo.setUser_id(resultSet.getInt("driver_id"));
        grabInfo.setPassenger_order_id(resultSet.getInt("passenger_order_id"));
        grabInfo.setCreate_time(Utils.checkTime(resultSet.getString("create_time")));
        grabInfo.setStatus(resultSet.getInt("status"));
        grabInfo.setSource(resultSet.getInt("source"));

        grabInfo.setUser_mobile(resultSet.getString("user_mobile"));
        grabInfo.setUser_avatar(resultSet.getString("user_avatar"));
        grabInfo.setUser_name(resultSet.getString("user_name"));
        grabInfo.setIdsn(resultSet.getString("idsn"));
        grabInfo.setCar_id(resultSet.getString("car_id"));
        grabInfo.setCar_owner(resultSet.getString("car_owner"));
        grabInfo.setCar_brand(resultSet.getString("car_brand"));
        grabInfo.setCar_type(resultSet.getString("car_type"));
        grabInfo.setCar_color(resultSet.getString("car_color"));



        return grabInfo;
    }
}
