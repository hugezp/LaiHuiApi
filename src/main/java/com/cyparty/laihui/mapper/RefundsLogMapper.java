package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Business;
import com.cyparty.laihui.domain.RefundsLog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by YangGuang on 2017/7/10.
 */
public class RefundsLogMapper implements RowMapper<RefundsLog> {

    @Override
    public RefundsLog mapRow(ResultSet resultSet, int i) throws SQLException {
        RefundsLog refundsLog = new RefundsLog();
        refundsLog.setRefundsId(resultSet.getInt("refunds_id"));
        refundsLog.setOutTradeNo(resultSet.getString("out_trade_no"));
        refundsLog.setRefundsPrice(resultSet.getDouble("refunds_price"));
        refundsLog.setRefundsTime(resultSet.getString("refunds_time"));
        refundsLog.setRefundsType(resultSet.getInt("refunds_type"));
        refundsLog.setUserId(resultSet.getInt("user_id"));
        return refundsLog;
    }
}

