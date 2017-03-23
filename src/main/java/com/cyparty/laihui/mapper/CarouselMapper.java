package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Carousel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class CarouselMapper implements RowMapper<Carousel> {

    public Carousel mapRow(ResultSet resultSet, int i) throws SQLException {
        Carousel carousel=new Carousel();

        carousel.set_id(resultSet.getInt("_id"));
        carousel.setImage_url(resultSet.getString("pc_image_url"));
        carousel.setImage_link(resultSet.getString("pc_image_link"));
        carousel.setImage_title(resultSet.getString("pc_image_title"));
        carousel.setImage_subtitle(resultSet.getString("pc_image_subtitle"));
        carousel.setSeq(resultSet.getInt("pc_image_seq"));
        carousel.setCreate_time(resultSet.getString("pc_image_create_time").split("\\.")[0]);
        carousel.setUpdate_time(resultSet.getString("pc_image_update_time").split("\\.")[0]);

        return carousel;
    }
}
