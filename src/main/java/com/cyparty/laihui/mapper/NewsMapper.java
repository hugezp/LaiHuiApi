package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.News;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by pangzhenpeng on 2017/6/5.
 */
public class NewsMapper implements RowMapper<News> {
    @Override
    public News mapRow(ResultSet resultSet, int i) throws SQLException {
        News news = new News();
        news.setId(resultSet.getInt("_id"));
        news.setTitle(Utils.checkNull(resultSet.getString("title")));
        news.setDescription(Utils.checkNull(resultSet.getString("description")));
        news.setContent(Utils.checkNull(resultSet.getString("content")));
        news.setCreateTime(Utils.checkNull(resultSet.getString("create_time")));
        news.setIsDel(resultSet.getInt("isDel"));
        news.setPublisher(Utils.checkNull(resultSet.getString("publisher")));
        news.setNewsType(resultSet.getInt("type"));
        news.setImage(Utils.checkNull(resultSet.getString("image")));
        news.setIsEnable(resultSet.getInt("is_enable"));
        news.setTypeName(Utils.checkNull(resultSet.getString("type_name")));
        news.setTypeId(resultSet.getInt("type_id"));
        news.setLogo(Utils.checkNull(resultSet.getString("logo")));
        return news;
    }
}
