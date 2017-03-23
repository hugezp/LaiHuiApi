package com.cyparty.laihui.mapper;


import com.cyparty.laihui.domain.CarTypeData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class CarTypeMapper implements RowMapper<CarTypeData> {

    public CarTypeData mapRow(ResultSet resultSet, int i) throws SQLException {
        CarTypeData carTypeData=new CarTypeData();

        carTypeData.set_id(resultSet.getInt("_id"));
        carTypeData.setCar_font_letter(resultSet.getString("car_font_letter"));
        carTypeData.setLogo(resultSet.getString("car_logo"));
        carTypeData.setBrand_id(resultSet.getString("car_brand_id"));
        carTypeData.setName(resultSet.getString("car_brand_name"));
        carTypeData.setBrand_type_id(resultSet.getString("car_brand_type_id"));
        carTypeData.setBrand_type_name(resultSet.getString("car_brand_type_name"));


        return carTypeData;
    }
}
