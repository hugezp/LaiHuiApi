package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.News;
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
        news.setTitle(resultSet.getString("title"));
        news.setDescription(resultSet.getString("description"));
        news.setContent(resultSet.getString("content"));
        news.setCreateTime(resultSet.getString("create_time"));
        news.setIsDel(resultSet.getInt("isDel"));
        news.setPublisher(resultSet.getString("publisher"));
        news.setNewsType(resultSet.getInt("type"));
        news.setImage(resultSet.getString("image"));
        news.setIsEnable(resultSet.getInt("is_enable"));
        news.setTypeName(resultSet.getString("type_name"));
        news.setTypeId(resultSet.getInt("type_id"));
        return news;
    }
}
