package com.cyparty.laihui.mapper;
import com.cyparty.laihui.domain.UserDriverLicenseInfo;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by dupei on 2017/4/8 0008.
 */
public class UserDriverLicenseInfoMapper implements RowMapper<UserDriverLicenseInfo> {
    public UserDriverLicenseInfo mapRow(ResultSet resultSet, int i) throws SQLException {
        UserDriverLicenseInfo driverLicenseInfo = new UserDriverLicenseInfo();
        driverLicenseInfo.set_id(resultSet.getInt("_id"));
        driverLicenseInfo.setUser_id(resultSet.getInt("user_id"));
        driverLicenseInfo.setDriver_name(Utils.checkNull(resultSet.getString("driver_name")));
        driverLicenseInfo.setDriver_license_number(Utils.checkNull(resultSet.getString("driver_license_number")));
        driverLicenseInfo.setFirst_issue_date(Utils.checkNull(resultSet.getString("first_issue_date")));
        driverLicenseInfo.setAllow_car_type(Utils.checkNull(resultSet.getString("allow_car_type")));
        driverLicenseInfo.setEffective_date_end(Utils.checkNull(resultSet.getString("effective_date_end")));
        driverLicenseInfo.setEffective_date_start(Utils.checkNull(resultSet.getString("effective_date_start")));
        driverLicenseInfo.setCreate_time(Utils.checkNull(resultSet.getString("create_time")));
        driverLicenseInfo.setDriver_license_photo(Utils.checkNull(resultSet.getString("driver_license_photo")));
        driverLicenseInfo.setIs_enable(Utils.checkNull(resultSet.getString("is_enable")));
        return driverLicenseInfo;
    }
}
