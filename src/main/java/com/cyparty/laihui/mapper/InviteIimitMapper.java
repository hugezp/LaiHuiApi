package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.InviteIimit;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/3/17.
 */
public class InviteIimitMapper implements RowMapper<InviteIimit> {

    @Override
    public InviteIimit mapRow(ResultSet resultSet, int i) throws SQLException {

        InviteIimit inviteIimit = new InviteIimit();
        inviteIimit.set_id(resultSet.getInt("_id"));
        inviteIimit.setPassenger_id(resultSet.getInt("passenger_id"));
        inviteIimit.setDriver_id(resultSet.getInt("driver_id"));
        inviteIimit.setInvite_time(resultSet.getString("invite_time"));
        return inviteIimit;
    }
}
