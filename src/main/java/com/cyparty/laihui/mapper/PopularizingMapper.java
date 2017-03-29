package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Popularizing;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by dupei on 2017/3/27 0027.
 */
public class PopularizingMapper implements RowMapper<Popularizing> {
    public  Popularizing mapRow (ResultSet resultSet, int i) throws SQLException{
        Popularizing popularize = new Popularizing();
        popularize.setId(resultSet.getInt("_id"));
        popularize.setPopularize_code(resultSet.getString("popularize_code"));
        popularize.setPopularizing_mobile(resultSet.getString("popularizing_mobile"));
        popularize.setCreate_time(Utils.checkNull(resultSet.getString("create_time")));
        return popularize;
    }
}
