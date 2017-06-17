package com.cyparty.laihui.service;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.domain.Order;
import com.cyparty.laihui.domain.PassengerOrder;
import com.cyparty.laihui.domain.User;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.NotifyPush;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 必达单订单相关的service
 * Created by YangGuang on 2017/6/17.
 */
public class ArriveOrderService {

    /**
     * 乘客同意车主抢单
     */
    public static String passengerAgree(AppDB appDB, HttpServletRequest request) {
        JSONObject result = new JSONObject();
        String json = "";
        String confirm_time = Utils.getCurrentTime();
        try {
            //判断用户标识
            String token = request.getParameter("token");
            if (token != null && token.length() == 32) {
                int car_id = Integer.parseInt(request.getParameter("car_id"));
                String where = " where a.is_enable = 1 and a._id = " + car_id;
                List<PassengerOrder> passengerOrderList = appDB.getPassengerDepartureInfo(where);
                if (passengerOrderList.size() > 0) {
                    String update_sql = " set order_status=2 ,update_time='" + confirm_time + "' where order_type = 2 and order_id=" + car_id;
                    appDB.update("pc_orders", update_sql);//司机抢单记录状态
                    String updateSql = " set order_status = 1 where _id = " + car_id;
                    appDB.update("pc_passenger_publish_info",updateSql);
                    //推送给车主
                    String uwhere = " where _id =" + appDB.getIDByToken(token);
                    User user = appDB.getUserList(uwhere).get(0);
                    String content = "乘客" + user.getUser_nick_name() + "在" + confirm_time + "确认了您的抢单！";
                    //乘客信息，司机信息，乘客订单信息
                    String where_order = " a left join pc_passenger_publish_info b on a.order_id=b._id where b._id=" + car_id + " and b.is_enable=1 and b.order_status=1 and departure_time>='" + Utils.getCurrentTime() + "'";
                    List<Order> orderList = appDB.getOrderReview(where_order, 2);
                    Order passengerOrder = orderList.get(0);
                    JSONObject passengerData = AppJsonUtils.getPushObject(appDB, passengerOrder, 1);
                    int push_type = 21;
                    int push_id = user.getUser_id();
                    String dwhere = "a left join pc_user b on a.user_id = b._id where a.order_type = 2 and a.is_enable = 1 and a.order_id = " + car_id;
                    List<Order> driverOrderList = appDB.getOrderReview(dwhere, 1);
                    String d_mobile = driverOrderList.get(0).getUser_mobile();
                    int order_id = driverOrderList.get(0).getOrder_id();
                    int id = driverOrderList.get(0).getUser_id();
                    boolean is_true = appDB.createPush(passengerOrder.get_id(), push_id, id, push_type, content, push_type, push_type + ".caf", passengerData.toJSONString(), 1, user.getUser_nick_name(), "");
                    if (is_true) {
                        NotifyPush.pinCheNotify("21", d_mobile, content, order_id, passengerData, confirm_time);
                    }
                    json = AppJsonUtils.returnSuccessJsonString(result, "处理成功！");
                    return json;
                } else {
                    result.put("error_code", ErrorCode.getBooking_order_is_not_existing());
                    json = AppJsonUtils.returnFailJsonString(result, "该订单已失效或未被抢单！");
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

    /**
     * 乘客拒绝车主抢单
     */
    public static String passengerRefuse(AppDB appDB, HttpServletRequest request) {
        JSONObject result = new JSONObject();
        String json = "";
        String confirm_time = Utils.getCurrentTime();
        try {
            //判断用户标识
            String token = request.getParameter("token");
            if (token != null && token.length() == 32) {
                int car_id = Integer.parseInt(request.getParameter("car_id"));
                String where = " where a.is_enable = 1 and a.order_status = 1 and a._id = " + car_id;
                List<PassengerOrder> passengerOrderList = appDB.getPassengerDepartureInfo(where);
                if (passengerOrderList.size() > 0) {
                    int refuse = passengerOrderList.get(0).getRefuse();
                    String tradeNo = passengerOrderList.get(0).getTrade_no();
                    String update_sql1 = " set order_status=4 ,is_enable = 0,update_time='" + confirm_time + "' where order_type = 2 and order_id=" + car_id;
                    appDB.update("pc_orders", update_sql1);//司机抢单记录状态
                    String update_sql2 = " set order_status=0,refuse = " + (refuse + 1) + " where _id=" + car_id;
                    appDB.update("pc_passenger_publish_info", update_sql2);//乘客车单状态
                    if (refuse == 2) {
                        String updateSql = " set is_arrive = 0 where _id = " + car_id;
                        appDB.update("pc_passenger_publish_info", updateSql);
                        String updateSql1 = " set is_del = 0 where = order_no = " + tradeNo;
                        appDB.update("arrive_driver_relation",update_sql1);
                    }
                    //推送给车主
                    String uwhere = " where _id =" + appDB.getIDByToken(token);
                    User user = appDB.getUserList(uwhere).get(0);
                    String content = "乘客" + user.getUser_nick_name() + "在" + confirm_time + "拒绝了您的抢单！";
                    //乘客信息，司机信息，乘客订单信息
                    String where_order = " a left join pc_passenger_publish_info b on a.order_id=b._id where b._id=" + car_id + " and b.is_enable=1 and b.order_status=1 and departure_time>='" + Utils.getCurrentTime() + "'";
                    List<Order> orderList = appDB.getOrderReview(where_order, 2);
                    Order passengerOrder = orderList.get(0);
                    JSONObject passengerData = AppJsonUtils.getPushObject(appDB, passengerOrder, 1);
                    int push_type = 21;
                    int push_id = user.getUser_id();
                    String dwhere = "a left join pc_user b on a.user_id = b._id where a.order_type = 2 and a.is_enable = 1 and a.order_id = " + car_id;
                    List<Order> driverOrderList = appDB.getOrderReview(dwhere, 1);
                    String d_mobile = driverOrderList.get(0).getUser_mobile();
                    int order_id = driverOrderList.get(0).getOrder_id();
                    int id = driverOrderList.get(0).getUser_id();
                    boolean is_true = appDB.createPush(passengerOrder.get_id(), push_id, id, push_type, content, push_type, push_type + ".caf", passengerData.toJSONString(), 1, user.getUser_nick_name(), "");
                    if (is_true) {
                        NotifyPush.pinCheNotify("21", d_mobile, content, order_id, passengerData, confirm_time);
                    }
                    json = AppJsonUtils.returnSuccessJsonString(result, "处理成功！");
                    return json;
                } else {
                    result.put("error_code", ErrorCode.getBooking_order_is_not_existing());
                    json = AppJsonUtils.returnFailJsonString(result, "该订单已失效或未被抢单！");
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
