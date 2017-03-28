package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.CarOwnerInfo;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class CarOwnerMapper implements RowMapper<CarOwnerInfo> {

    public CarOwnerInfo mapRow(ResultSet resultSet, int i) throws SQLException {
        CarOwnerInfo carOwnerInfo=new CarOwnerInfo();

        carOwnerInfo.set_id(resultSet.getInt("_id"));
        carOwnerInfo.setUser_id(resultSet.getInt("user_id"));
        carOwnerInfo.setCar_owner_name(resultSet.getString("car_owner_name"));
        String idsn= Utils.checkNull(resultSet.getString("idsn"));
        /*if(!idsn.trim().equals("")){
            idsn=idsn.substring(0,3)+"******"+idsn.substring(14);
        }*/
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
        if(resultSet.getString("car_validate_date")!=null){
            validate_date=validate_date.split("\\.")[0];
        }
        carOwnerInfo.setCar_reg_date(reg_date);
        carOwnerInfo.setCar_validate_date(validate_date);

        String name= Utils.checkNull(resultSet.getString("user_name"));

        if(!name.isEmpty()) {
            String endName = "";
            String sexNum ="";
            if (!idsn.isEmpty()) {
                int length = idsn.length();
                switch (length){
                    case 15:
                        if (!idsn.substring(14,15).matches("[a-zA-Z]")){
                            sexNum = idsn.substring(14,15);
                        }
                        break;
                    case 18:
                        sexNum = idsn.substring(16,17);
                        break;
                    default:
                        sexNum = "1";
                }
                if (!sexNum.isEmpty()) {
                    if (Integer.parseInt(sexNum) % 2 == 1) {
                        endName = "先生";
                    } else {
                        endName = "女士";
                    }
                }
            }
            if (name.length() <= 3) {
                name = name.substring(0, 1) + endName;
            } else {
                name = name.substring(0, 2) + endName;
            }
        }
        carOwnerInfo.setUser_name(name);
        carOwnerInfo.setMobile(Utils.checkNull(resultSet.getString("user_mobile")));
        carOwnerInfo.setUser_avatar(Utils.checkNull(resultSet.getString("user_avatar")));

        return carOwnerInfo;
    }
}
