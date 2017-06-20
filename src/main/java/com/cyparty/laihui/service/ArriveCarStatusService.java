package com.cyparty.laihui.service;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 必达模块车单状态
 * Created by YangGuang on 2017/6/19.
 */
public class ArriveCarStatusService {

    /**
     * 乘客端车单状态1
     */
    public static String passengerStatus(HttpServletRequest request, AppDB appDB) {
        JSONObject result = new JSONObject();
        String json = "";
        String confirm_time = Utils.getCurrentTime();
        int orderId = 0;
        try {
            //判断用户标识
            String token = request.getParameter("token");
            if (token != null && token.length() == 32) {
                String uwhere = " where _id =" + appDB.getIDByToken(token);
                User user = appDB.getUserList(uwhere).get(0);
                int userId = user.getUser_id();
                orderId = Integer.parseInt(request.getParameter("order_id"));
                String where = " a right join pc_passenger_publish_info b on a.order_id=b._id where a.is_enable=1 and a.order_type=2 and a.order_id in (SELECT order_id from pc_orders where  order_type=0  and _id=" + orderId + " and user_id=" + userId + ")";
                where = where + " order by a.create_time DESC limit 1 ";
                List<Order> orderList = appDB.getOrderReview(where, 2);
                if (orderList.size() > 0) {
                    Order order = orderList.get(0);
                    int status = order.getOrder_status();
                    result.put("status", status);
                    result.put("isArrive", order.getIsArrive());
                    //得到司机手机号
                    where = " where _id=" + order.getDriver_id();
                    List<User> drivers = appDB.getUserList(where);
                    if (drivers.size() > 0) {
                        User driver = drivers.get(0);
                        result.put("mobile", driver.getUser_mobile());
                        result.put("name", driver.getUser_nick_name());
                        result.put("avatar", driver.getAvatar());
                    } else {
                        result.put("mobile", "");
                        result.put("name", "");
                        result.put("avatar", "");
                    }
                    List<CarOwnerInfo> carOwnerInfos = appDB.getCarOwnerInfo("where a.user_id=" + order.getDriver_id());
                    if (carOwnerInfos.size() > 0) {
                        CarOwnerInfo user1 = carOwnerInfos.get(0);
                        if (user1.getFlag() == 0) {
                            result.put("car", user1.getCar_id());
                            result.put("car_brand", user1.getCar_brand());
                            result.put("car_color", user1.getCar_color());
                            result.put("car_type", user1.getCar_type());
                        } else {
                            List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(order.getDriver_id());
                            UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                            result.put("car", travelCardInfo.getCar_license_number());
                            if (travelCardInfos.size() > 0) {
                                result.put("car_brand", "");
                                result.put("car_color", travelCardInfo.getCar_color());
                                result.put("car_type", travelCardInfo.getCar_type());
                            }
                        }
                    } else {
                        List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(order.getDriver_id());
                        if (travelCardInfos.size() > 0) {
                            UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                            result.put("car", travelCardInfo.getCar_license_number());
                            result.put("car_brand", "");
                            result.put("car_color", travelCardInfo.getCar_color());
                            result.put("car_type", travelCardInfo.getCar_type());
                        } else {
                            result.put("car", "");
                            result.put("car_brand", "");
                            result.put("car_color", "");
                            result.put("car_type", "");
                        }
                    }
                    json = AppJsonUtils.returnSuccessJsonString(result, "车主信息获取成功！");
                    return json;
                } else {
                    json = AppJsonUtils.returnFailJsonString(result, "暂无订单数据！");
                    return json;
                }
            } else {
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return json;
            }
        } catch (Exception e) {
            result.put("error_code", ErrorCode.getError_system());
            json = AppJsonUtils.returnFailJsonString(result, "服务器错误！");
            return json;
        }
    }
}
