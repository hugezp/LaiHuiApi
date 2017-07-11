package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.CrossCity;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class CrossCity1Mapper implements RowMapper<CrossCity> {

    public CrossCity mapRow(ResultSet resultSet, int i) throws SQLException {
        CrossCity departure=new CrossCity();

        departure.setR_id(resultSet.getInt("_id"));
        departure.setStart_time(Utils.checkTime(resultSet.getString("departure_time")));
        departure.setCreate_time(Utils.checkTime(resultSet.getString("create_time")));
        departure.setBoarding_point(resultSet.getString("boarding_point"));
        departure.setBreakout_point(resultSet.getString("breakout_point"));
        departure.setDeparture_address_code(resultSet.getInt("departure_address_code"));
        departure.setDestination_address_code(resultSet.getInt("destination_address_code"));
        departure.setDeparture_city_code(resultSet.getInt("departure_city_code"));
        departure.setDestination_city_code(resultSet.getInt("destination_city_code"));
        departure.setSurchargeType(resultSet.getInt("surcharge_type"));
        departure.setSurchargeMoney(resultSet.getDouble("surcharge_money"));
        return departure;
    }
}
