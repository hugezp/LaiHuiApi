package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.PassengerOrder;
import com.cyparty.laihui.domain.PassengerPublishInfo;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class PassengerPublishInfoMapper implements RowMapper<PassengerOrder> {

    public PassengerOrder mapRow(ResultSet resultSet, int i) throws SQLException {
        PassengerOrder passenger=new PassengerOrder();

        passenger.set_id(resultSet.getInt("_id"));
        passenger.setUser_id(resultSet.getInt("user_id"));
        passenger.setP_id(resultSet.getInt("p_id"));

        passenger.setIs_enable(resultSet.getInt("a.is_enable"));
        passenger.setDeparture_time(Utils.checkTime(resultSet.getString("departure_time")));
        //新增备注字段
        passenger.setRemark(Utils.checkNull(resultSet.getString("remark")));
        passenger.setBoarding_point(Utils.checkNull(resultSet.getString("boarding_point")));
        passenger.setBreakout_point(Utils.checkNull(resultSet.getString("breakout_point")));
        passenger.setSeats(resultSet.getInt("booking_seats"));
        passenger.setDescription(Utils.checkNull(resultSet.getString("description")));
        passenger.setCreate_time(resultSet.getString("create_time").split("\\.")[0]);
        passenger.setorder_status(resultSet.getInt("order_status"));
        passenger.setPay_money(resultSet.getDouble("price"));
        passenger.setPay_num(resultSet.getString("trade_no"));
        passenger.setSource(resultSet.getInt("source"));
        passenger.setBoarding_longitude(resultSet.getString("boarding_longitude"));
        passenger.setBoarding_latitude(resultSet.getString("boarding_latitude"));
        passenger.setBreakout_longitude(resultSet.getString("breakout_longitude"));
        passenger.setBreakout_latitude(resultSet.getString("breakout_latitude"));
        passenger.setDeparture_code(resultSet.getInt("departure_code"));
        passenger.setDestination_code(resultSet.getInt("destination_code"));

        String name= Utils.checkNull(resultSet.getString("user_name"));
        String idsn= Utils.checkNull(resultSet.getString("user_idsn"));

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
        passenger.setUser_name(name);
        passenger.setUser_avatar(Utils.checkNull(resultSet.getString("user_avatar")));
        passenger.setMobile(Utils.checkNull(resultSet.getString("user_mobile")));
        return passenger;
    }
}
