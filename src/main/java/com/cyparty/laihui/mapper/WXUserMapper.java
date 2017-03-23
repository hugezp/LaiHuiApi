package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.WXUser;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhu on 2016/8/31.
 */
public class WXUserMapper implements RowMapper<WXUser> {
    @Override
    public WXUser mapRow(ResultSet resultSet, int i) throws SQLException {
        WXUser user=new WXUser();
        user.setUser_id(resultSet.getInt("user_id"));
        user.setUser_mobile(Utils.checkNull(resultSet.getString("user_mobile")));
        user.setUser_nickname(Utils.checkNull(resultSet.getString("user_name")));
        user.setUser_created(Utils.checkNull(resultSet.getString("user_create_time")));
        user.setUser_token(Utils.checkNull(resultSet.getString("user_wx_token")));
        user.setUser_avatar(Utils.checkNull(resultSet.getString("user_avatar")));
        user.setSex(resultSet.getInt("user_sex"));
        return user;
    }


}
