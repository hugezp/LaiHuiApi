package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.CarOwnerInfo;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class CarOwnerTokenMapper implements RowMapper<CarOwnerInfo> {

    public CarOwnerInfo mapRow(ResultSet resultSet, int i) throws SQLException {
        CarOwnerInfo carOwnerInfo=new CarOwnerInfo();

        carOwnerInfo.set_id(resultSet.getInt("_id"));
        carOwnerInfo.setToken(resultSet.getString("token"));
        carOwnerInfo.setCar_owner_name(resultSet.getString("car_owner_name"));
        String idsn= Utils.checkNull(resultSet.getString("idsn"));
        if(!idsn.trim().equals("")){
            idsn=idsn.substring(0,3)+"******"+idsn.substring(14);
        }
        carOwnerInfo.setIdsn(idsn);
        carOwnerInfo.setCar_owner(resultSet.getString("car_owner"));
        carOwnerInfo.setPic_licence(resultSet.getString("pic_licence"));
        carOwnerInfo.setPic_licence2(resultSet.getString("pic_licence2"));
        carOwnerInfo.setCar_id(resultSet.getString("car_id"));
        carOwnerInfo.setCar_brand(resultSet.getString("car_brand"));
        carOwnerInfo.setCar_type(resultSet.getString("car_type"));
        carOwnerInfo.setCar_color(resultSet.getString("car_color"));
        String reg_date=resultSet.getString("car_reg_date");
        if(resultSet.getString("car_reg_date")!=null){
            reg_date=reg_date.split("\\.")[0].split(" ")[0];
        }
        String validate_date=resultSet.getString("car_validate_date");
        String checked_date= Utils.checkNull(resultSet.getString("checked_time"));
        if(!checked_date.trim().equals("")){
            checked_date=checked_date.split("\\.")[0];
        }else {
            checked_date="待审核";
        }
        if(resultSet.getString("car_validate_date")!=null){
            validate_date=validate_date.split("\\.")[0];
        }
        carOwnerInfo.setChecked_time(checked_date);
        carOwnerInfo.setCar_reg_date(reg_date);
        carOwnerInfo.setCar_validate_date(validate_date);

        return carOwnerInfo;
    }
}
