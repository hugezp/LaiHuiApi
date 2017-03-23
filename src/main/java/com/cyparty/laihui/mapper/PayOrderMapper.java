package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.PayOrder;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class PayOrderMapper implements RowMapper<PayOrder> {

    public PayOrder mapRow(ResultSet resultSet, int i) throws SQLException {
        PayOrder pay=new PayOrder();

        pay.setUser_name(Utils.checkNull(resultSet.getString("user_name")));
        pay.setUser_id(resultSet.getInt("user_id"));
        pay.setUser_mobile(resultSet.getString("mobile"));
        pay.setOrder_id(resultSet.getInt("order_id"));
        pay.setPay_money(resultSet.getDouble("pay_money"));
        pay.setPay_num(resultSet.getString("pay_num"));
        pay.setCreate_time(resultSet.getString("create_time").split("\\.")[0]);
        pay.setPay_status(resultSet.getInt("pay_status"));
        return pay;
    }
}
