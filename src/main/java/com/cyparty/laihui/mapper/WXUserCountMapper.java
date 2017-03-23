package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.WXUserCount;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class WXUserCountMapper implements RowMapper<WXUserCount> {

    public WXUserCount mapRow(ResultSet resultSet, int i) throws SQLException {
        WXUserCount count=new WXUserCount();

        count.setCreate_time(resultSet.getString("create_date"));
        count.setTotal(resultSet.getInt("number"));

        return count;
    }
}
