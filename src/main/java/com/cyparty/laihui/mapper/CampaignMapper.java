package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.Campaign;
import com.cyparty.laihui.domain.Code;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2015/12/29.
 */
public class CampaignMapper implements RowMapper<Campaign> {

    public Campaign mapRow(ResultSet resultSet, int i) throws SQLException {
        Campaign campaign=new Campaign();

        campaign.setUser_id(resultSet.getInt("user_id"));
        campaign.setBe_popularized_mobile(resultSet.getString("be_popularized_mobile"));

        return campaign;
    }
}
