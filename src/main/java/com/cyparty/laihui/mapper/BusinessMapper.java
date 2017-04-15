package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Business;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/4/13.
 */
public class BusinessMapper implements RowMapper<Business> {

    @Override
    public Business mapRow(ResultSet resultSet, int i) throws SQLException {
        Business business = new Business();
        business.setBusiness_name(resultSet.getString("business_name"));
        business.setBusiness_mobile(resultSet.getString("business_mobile"));
//        business.set_id(resultSet.getInt("_id"));
//        business.setBusiness_name(resultSet.getString("business_name"));
//        business.setBusiness_mobile(resultSet.getString("business_mobile"));
//        business.setAddress(resultSet.getString("address"));
//        business.setCooperation_way(resultSet.getString("cooperation_way"));
//        business.setCooperation_description(resultSet.getString("cooperation_description"));
//        business.setCreate_time(resultSet.getString("create_time"));
//        business.setFlag(resultSet.getInt("flag"));

        return business;
    }
}

