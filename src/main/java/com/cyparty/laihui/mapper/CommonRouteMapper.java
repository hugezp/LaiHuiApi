package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.CommonRoute;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/3/9.
 */
public class CommonRouteMapper implements RowMapper<CommonRoute> {
    @Override
    public CommonRoute mapRow(ResultSet rs, int rowNum) throws SQLException {
        CommonRoute commonRoute = new CommonRoute();
        commonRoute.setDeparture_city(rs.getString("departure_city"));
        commonRoute.setDeparture_address(rs.getString("departure_address"));
        commonRoute.setDeparture_lat(rs.getString("departure_lat"));
        commonRoute.setDeparture_lon(rs.getString("departure_lon"));
        commonRoute.setDestinat_city(rs.getString("destinat_city"));
        commonRoute.setDestinat_address(rs.getString("destinat_address"));
        commonRoute.setDestinat_lat(rs.getString("destinat_lat"));
        commonRoute.setDestinat_lon(rs.getString("destinat_lon"));
        commonRoute.setIs_enable(rs.getInt("is_enable"));
        commonRoute.setId(rs.getInt("id"));
        commonRoute.setUser_id(rs.getInt("user_id"));

        return commonRoute;
    }
}
