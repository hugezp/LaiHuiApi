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
        push.setAlert(Utils.checkNull(resultSet.getString("alert")));
        push.setType(resultSet.getInt("type"));
        push.setSound(Utils.checkNull(resultSet.getString("sound")));
        push.setData(Utils.checkNull(resultSet.getString("data")));
        push.setTime(Utils.checkTime(resultSet.getString("time")));
        push.setStatus(resultSet.getInt("status"));
        push.setIs_enable(resultSet.getInt("is_enable"));
        push.setUser_name(Utils.checkNull(resultSet.getString("user_name")));
        push.setLink_url(Utils.checkNull(resultSet.getString("link_url")));
        push.setTitle(Utils.checkNull(resultSet.getString("title")));
        push.setIsArrive(resultSet.getInt("is_arrive"));
        push.setImageUrl(Utils.checkNull(resultSet.getString("image_url")));
        return push;
    }
}
