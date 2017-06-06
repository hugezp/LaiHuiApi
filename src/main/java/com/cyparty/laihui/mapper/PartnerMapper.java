package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Partner;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by pangzhenpeng on 2017/6/3.
 */
public class PartnerMapper implements RowMapper<Partner> {
    @Override
    public Partner mapRow(ResultSet resultSet, int i) throws SQLException {
        Partner partner = new Partner();
        partner.setId(resultSet.getInt("_id"));
        partner.setPartnerIcon(resultSet.getString("Partner_icon"));
        partner.setPartnerIconUrl(resultSet.getString("Partner_icon_url"));
        partner.setPartnerUrl(resultSet.getString("Partner_url"));
        partner.setIsEnable(resultSet.getInt("is_enable"));
        return partner;
    }
}
