package com.cyparty.laihui.mapper;

import com.cyparty.laihui.domain.UserSuggestion;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**gengxin
 * Created by zhu on 2015/12/29.
 */
public class UserSuggestionMapper implements RowMapper<UserSuggestion> {

    public UserSuggestion mapRow(ResultSet resultSet, int i) throws SQLException {
        UserSuggestion suggestion=new UserSuggestion();

        suggestion.setUser_id(resultSet.getInt("user_id"));
        suggestion.set_id(resultSet.getInt("_id"));
        suggestion.setContent(resultSet.getString("advice"));
        suggestion.setCreate_time(resultSet.getString("create_time").split("\\.")[0]);
        suggestion.setUser_name(Utils.checkNull(resultSet.getString("user_name")));
        suggestion.setUser_avatar(Utils.checkNull(resultSet.getString("user_avatar")));
        suggestion.setUser_mobile(Utils.checkNull(resultSet.getString("user_mobile")));


        return suggestion;
    }
}
