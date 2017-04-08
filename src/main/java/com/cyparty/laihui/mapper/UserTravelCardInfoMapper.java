package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.UserTravelCardInfo;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by dupei on 2017/4/8 0008.
 */
public class UserTravelCardInfoMapper implements RowMapper<UserTravelCardInfo> {
    public UserTravelCardInfo mapRow(ResultSet resultSet ,int i)throws SQLException{
        UserTravelCardInfo userTravelCardInfo = new UserTravelCardInfo();
        userTravelCardInfo.set_id(resultSet.getInt("_id"));
        userTravelCardInfo.setUser_id(resultSet.getInt("user_id"));
        userTravelCardInfo.setCar_license_number(Utils.checkNull(resultSet.getString("car_license_number")));
        userTravelCardInfo.setCar_color(Utils.checkNull(resultSet.getString("car_color")));
        userTravelCardInfo.setCar_type(Utils.checkNull(resultSet.getString("car_type")));
        userTravelCardInfo.setRegistration_date(Utils.checkNull(resultSet.getString("registration_date")));
        userTravelCardInfo.setCreate_time(Utils.checkNull(resultSet.getString("create_time")));
        userTravelCardInfo.setTravel_license_photo(Utils.checkNull(resultSet.getString("travel_license_photo")));
        userTravelCardInfo.setVehicle_owner_name(Utils.checkNull(resultSet.getString("vehicle_owner_name")));
        userTravelCardInfo.setIs_enable(Utils.checkNull(resultSet.getString("is_enable")));
        return userTravelCardInfo;
    }
}
