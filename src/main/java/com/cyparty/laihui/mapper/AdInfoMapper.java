package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Adviertisement;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class AdInfoMapper implements RowMapper<Adviertisement> {

    public Adviertisement mapRow(ResultSet resultSet, int i) throws SQLException {
        Adviertisement adviertisement=new Adviertisement();

        adviertisement.setAd_id(resultSet.getInt("ad_id"));
        adviertisement.setDest_name(resultSet.getString("dest_name"));
        adviertisement.setAd_pic_url(resultSet.getString("ad_pic_url"));
        adviertisement.setAd_link(resultSet.getString("ad_link"));
        adviertisement.setAd_weight(resultSet.getInt("ad_weight"));
        adviertisement.setAd_updated_time(resultSet.getString("ad_updated_time"));
        adviertisement.setAd_create_time(resultSet.getString("ad_create_time"));

        return adviertisement;
    }
}
