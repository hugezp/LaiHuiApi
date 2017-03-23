package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Adviertisement;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class AdCumulateMapper implements RowMapper<Adviertisement> {

    public Adviertisement mapRow(ResultSet resultSet, int i) throws SQLException {
        Adviertisement adviertisement=new Adviertisement();

        adviertisement.setAd_id(resultSet.getInt("_id"));
        adviertisement.setDest_name(resultSet.getString("dest_name"));
        adviertisement.setTotal(resultSet.getInt("total"));
        adviertisement.setTotal_weight(resultSet.getInt("total_weight"));
        adviertisement.setAd_updated_time(resultSet.getString("last_updated"));
        adviertisement.setAd_create_time(resultSet.getString("create_time"));

        return adviertisement;
    }
}
