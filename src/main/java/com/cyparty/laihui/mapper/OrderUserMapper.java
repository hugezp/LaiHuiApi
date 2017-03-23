package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Order;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class OrderUserMapper implements RowMapper<Order> {

    public Order mapRow(ResultSet resultSet, int i) throws SQLException {
        Order order=new Order();

        order.set_id(resultSet.getInt("a._id"));
        order.setOrder_id(resultSet.getInt("order_id"));
        order.setUser_id(resultSet.getInt("user_id"));
        order.setOrder_status(resultSet.getInt("order_status"));
        order.setUpdate_time(Utils.checkTime(resultSet.getString("update_time")));
        order.setCreate_time(Utils.checkTime(resultSet.getString("create_time")));
        String name= Utils.checkNull(resultSet.getString("user_name"));
        String idsn= Utils.checkNull(resultSet.getString("user_idsn"));

        if(!name.isEmpty()) {
            String endName = "";
            if (!idsn.isEmpty()) {
                String sexNum = idsn.substring(16, 17);
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
        order.setUser_name(name);
        order.setUser_mobile(Utils.checkNull(resultSet.getString("user_mobile")));
        order.setUser_avatar(Utils.checkNull(resultSet.getString("user_avatar")));
        return order;
    }
}
