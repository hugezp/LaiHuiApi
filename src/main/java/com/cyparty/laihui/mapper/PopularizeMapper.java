package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Popularize;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class PopularizeMapper implements RowMapper<Popularize> {
    public Popularize mapRow(ResultSet resultSet, int i) throws SQLException {
        Popularize popularize = new Popularize();
        popularize.setId(resultSet.getInt("_id"));
        popularize.setPopularize_id(resultSet.getInt("popularize_id"));
        popularize.setP_id(resultSet.getInt("p_id"));
        popularize.setPopularize_parent_id(resultSet.getInt("popularize_parent_id"));
        popularize.setPopularize_parents_id(resultSet.getString("popularize_parents_id"));
        popularize.setPopularize_code(resultSet.getString("popularize_code"));
        popularize.setCreate_time(Utils.checkNull(resultSet.getString("create_time")));
        popularize.setUpdate_time(Utils.checkNull(resultSet.getString("update_time")));
        popularize.setIs_enable(resultSet.getInt("is_enable"));
        popularize.setLevel(resultSet.getInt("level"));

        return popularize;
    }
}
