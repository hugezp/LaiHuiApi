package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.ArriveDriver;
import com.cyparty.laihui.domain.Carousel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 必达单车主映射
 * Created by YangGuang on 2017/6/16.
 */
public class ArrvieDriverMapper implements RowMapper<ArriveDriver> {

    public ArriveDriver mapRow(ResultSet resultSet, int i) throws SQLException {
        ArriveDriver arriveDriver=new ArriveDriver();

        arriveDriver.setrId(resultSet.getInt("r_id"));
        arriveDriver.setDriverPhone(resultSet.getString("driver_phone"));
        arriveDriver.setOrderNo(resultSet.getString("order_no"));
        arriveDriver.setPassengerId(resultSet.getInt("passenger_id"));
        arriveDriver.setCreateTime(resultSet.getString("create_time").split("\\.")[0]);
        arriveDriver.setIsDel(resultSet.getInt("is_del"));
        return arriveDriver;
    }
}
