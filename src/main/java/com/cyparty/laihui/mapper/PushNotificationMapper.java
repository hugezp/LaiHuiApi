package com.cyparty.laihui.mapper;
import com.cyparty.laihui.domain.PushNotification;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Created by Administrator on 2017/3/18 0018.
 */
public class PushNotificationMapper implements RowMapper<PushNotification>  {
    public PushNotification mapRow(ResultSet resultSet, int i) throws SQLException {
        PushNotification push = new PushNotification();
        push.set_id(resultSet.getInt("_id"));
        push.setOrder_id(resultSet.getInt("order_id"));
        push.setPush_id(resultSet.getInt("push_id"));
        push.setReceive_id(resultSet.getInt("receive_id"));
        push.setPush_type(resultSet.getInt("push_type"));
        push.setAlert(resultSet.getString("alert"));
        push.setType(resultSet.getInt("type"));
        push.setSound(resultSet.getString("sound"));
        push.setData(resultSet.getString("data"));
        push.setTime(Utils.checkTime(resultSet.getString("time")));
        push.setStatus(resultSet.getInt("status"));
        push.setIs_enable(resultSet.getInt("is_enable"));
        push.setUser_name(resultSet.getString("user_name"));
        push.setLink_url(resultSet.getString("link_url"));
        return push;
    }
}
