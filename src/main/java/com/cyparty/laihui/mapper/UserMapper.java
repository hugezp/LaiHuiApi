package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.User;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class UserMapper implements RowMapper<User> {

    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user=new User();

        user.setUser_id(resultSet.getInt("_id"));

        user.setUser_mobile(resultSet.getString("user_mobile"));
        user.setUser_name(Utils.checkNull(resultSet.getString("user_name")));
        user.setAvatar(Utils.checkNull(resultSet.getString("user_avatar")));
        String idsn= Utils.checkNull(resultSet.getString("user_idsn"));
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
                if (!sexNum.isEmpty()&&!sexNum.equals(" ")) {
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
        user.setUser_nick_name(name);
//        if(!idsn.trim().equals("")&&idsn.length()>17){
//            idsn=idsn.substring(0,3)+"******"+idsn.substring(14);
//        }
        user.setUser_idsn(idsn);
        String create_time=resultSet.getString("user_create_time");
        String last_login=resultSet.getString("user_last_login");
        if(create_time!=null){
            create_time=create_time.split("\\.")[0];
        }
        if(last_login!=null){
            last_login=last_login.split("\\.")[0];
        }

        user.setUser_create_time(create_time);
        user.setUser_last_login(last_login);
        user.setIs_validated(resultSet.getInt("is_validated"));
        user.setIs_car_owner(resultSet.getInt("is_car_owner"));
        user.setReason(Utils.checkNull(resultSet.getString("checked_unpass_reason")));
        user.setUser_last_login_ip(Utils.checkNull(resultSet.getString("user_last_login_ip")));
        user.setAvatar(Utils.checkNull(resultSet.getString("user_avatar")));
        user.setSignature(Utils.checkNull(resultSet.getString("signature")));
        user.setBirthday(Utils.checkNull(resultSet.getString("birthday")));
        user.setLive_city(Utils.checkNull(resultSet.getString("live_city")));
        user.setSex(Utils.checkNull(resultSet.getString("sex")));
        user.setCompany(Utils.checkNull(resultSet.getString("company")));
        user.setHome(Utils.checkNull(resultSet.getString("home")));
        user.setFlag(resultSet.getInt("flag"));
        user.setU_flag(resultSet.getInt("u_flag"));
        user.setDelivery_address(Utils.checkNull(resultSet.getString("delivery_address")));
        return user;
    }
}
