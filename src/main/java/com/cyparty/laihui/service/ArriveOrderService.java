package com.cyparty.laihui.service;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.NotifyPush;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 必达单订单相关的service
 * Created by YangGuang on 2017/6/17.
 */
@Component
@Transactional
public class ArriveOrderService {

    /**
     * 乘客同意车主抢单
     */
    public static String passengerAgree(AppDB appDB, HttpServletRequest request) throws RuntimeException {
        JSONObject result = new JSONObject();
        String json = "";
        String confirm_time = Utils.getCurrentTime();
        int source = 0;
        String now_source = request.getParameter("source");
        if (now_source != null && !now_source.isEmpty() && now_source.equals("iOS")) {
            source = 1;
        }
        //判断用户标识
        String token = request.getParameter("token");
        if (token != null && token.length() == 32) {
            int car_id = Integer.parseInt(request.getParameter("car_id"));
            String where = " where a.is_enable = 1 and a._id = " + car_id;
            List<PassengerOrder> passengerOrderList = appDB.getPassengerDepartureInfo(where);
            if (passengerOrderList.size() > 0) {
                String update_sql1 = " set order_status = 200 ,update_time='" + confirm_time + "' where order_type = 2 and order_id=" + car_id;
                appDB.update("pc_orders", update_sql1);//司机抢单记录状态
                String update_sql2 = " set order_status = 300 ,update_time='" + confirm_time + "' where order_type = 0 and order_id=" + car_id;
                appDB.update("pc_orders", update_sql2);//乘客抢单记录状态
                String updateSql = " set order_status = 1 where _id = " + car_id;
                appDB.update("pc_passenger_publish_info", updateSql);
                //推送给车主
                String uwhere = " where _id =" + appDB.getIDByToken(token);
                User user = appDB.getUserList(uwhere).get(0);
                String content = "乘客" + user.getUser_nick_name() + "在" + confirm_time + "确认了您的抢单！";
                //乘客信息，司机信息，乘客订单信息
                String where_order = " a left join pc_passenger_publish_info b on a.order_id=b._id where b._id=" + car_id + " and b.is_enable=1 and b.order_status=1 and departure_time>='" + Utils.getCurrentTime() + "'";
                List<Order> orderList = appDB.getOrderReview(where_order, 2);
                Order passengerOrder = orderList.get(0);
                JSONObject passengerData = AppJsonUtils.getPushObject(appDB, passengerOrder, 1);
                passengerData.put("isArrive", 1);
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
                defaultCreate(driverOrderList,source,car_id,appDB);
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
    }

    /**
     * 乘客拒绝车主抢单
     */
    public static String passengerRefuse(AppDB appDB, HttpServletRequest request) throws RuntimeException {
        JSONObject result = new JSONObject();
        String json = "";
        String confirm_time = Utils.getCurrentTime();
        //判断用户标识
        String token = request.getParameter("token");
        if (token != null && token.length() == 32) {
            int car_id = Integer.parseInt(request.getParameter("car_id"));
            String where = " where a.is_enable = 1 and a.order_status = 1 and a._id = " + car_id;
            List<PassengerOrder> passengerOrderList = appDB.getPassengerDepartureInfo(where);
            if (passengerOrderList.size() > 0) {
                String dwhere = "a left join pc_user b on a.user_id = b._id where a.order_type = 2 and a.is_enable = 1 and a.order_id = " + car_id;
                List<Order> driverOrderList = appDB.getOrderReview(dwhere, 1);
                String d_mobile = driverOrderList.get(0).getUser_mobile();
                int order_id = driverOrderList.get(0).getOrder_id();
                int id = driverOrderList.get(0).getUser_id();
                int refuse = passengerOrderList.get(0).getRefuse();
                String tradeNo = passengerOrderList.get(0).getTrade_no();
                String update_sql1 = " set order_status=4 ,is_enable = 0,update_time='" + confirm_time + "' where order_type = 2 and order_id=" + car_id;
                appDB.update("pc_orders", update_sql1);//司机抢单记录状态
                String update_sql2 = " set order_status=0,refuse = " + (refuse + 1) + " where _id=" + car_id;
                appDB.update("pc_passenger_publish_info", update_sql2);//乘客车单状态
                String update_sql3 = " set is_del = 0 where order_no = '" + tradeNo + "' and driver_phone = '" + d_mobile + "'";
                appDB.update("arrive_driver_relation", update_sql3);//该车主标记为普通车主
                String update_sql4 = " set order_status=200 ,update_time='" + confirm_time + "' where order_type = 0 and order_id=" + car_id;
                appDB.update("pc_orders", update_sql4);//乘客抢单记录状态
                if (refuse == 2) {
                    String updateSql = " set is_arrive = 0 where _id = " + car_id;
                    appDB.update("pc_passenger_publish_info", updateSql);
                    String updateSql1 = " set is_del = 0 where order_no = " + tradeNo;
                    appDB.update("arrive_driver_relation", updateSql1);
                    String updateSql2 = " set order_status = -1 where order_type = 0 and order_id=" + car_id;
                    appDB.update("pc_orders", updateSql2);
                }
                //推送给车主
                String uwhere = " where _id =" + appDB.getIDByToken(token);
                User user = appDB.getUserList(uwhere).get(0);
                String content = "乘客" + user.getUser_nick_name() + "在" + confirm_time + "拒绝了您的抢单！";
                //乘客信息，司机信息，乘客订单信息
                String where_order = " a left join pc_passenger_publish_info b on a.order_id=b._id where b._id=" + car_id + " and b.is_enable=1 and b.order_status=0 and departure_time>='" + Utils.getCurrentTime() + "'";
                List<Order> orderList = appDB.getOrderReview(where_order, 2);
                Order passengerOrder = orderList.get(0);
                JSONObject passengerData = AppJsonUtils.getPushObject(appDB, passengerOrder, 1);
                passengerData.put("isArrive", 1);
                passengerData.put("boarding_point", passengerOrder.getBoarding_point());
                passengerData.put("breakout_point", passengerOrder.getBreakout_point());
                passengerData.put("departure_time", passengerOrder.getDeparture_time());
                passengerData.put("seats", passengerOrder.getBooking_seats());
                passengerData.put("price", passengerOrder.getPrice());
                passengerData.put("order_id", passengerOrder.getOrder_id());
                passengerData.put("record_id", passengerOrder.get_id());
                int push_type = 22;
                int push_id = user.getUser_id();
                boolean is_true = appDB.createPush(passengerOrder.get_id(), push_id, id, push_type, content, push_type, push_type + ".caf", passengerData.toJSONString(), 1, user.getUser_nick_name(), "");
                if (is_true) {
                    NotifyPush.pinCheNotify("22", d_mobile, content, order_id, passengerData, confirm_time);
                }
                json = AppJsonUtils.returnSuccessJsonString(result, "处理成功！");
                return json;
            } else {
                result.put("error_code", ErrorCode.getBooking_order_is_not_existing());
                json = AppJsonUtils.returnFailJsonString(result, "请勿重复拒绝！");
                return json;

            }
        } else {
            result.put("error_code", ErrorCode.getToken_expired());
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return json;
        }
    }

    /**
     * 给车主创建车单
     */
    private static void defaultCreate(List<Order> driverOrderList, int source, int car_id, AppDB appDB) {

        String where = " where a._id = " + car_id;
        List<PassengerOrder> passengerOrderList = appDB.getPassengerDepartureInfo(where);
        Order order = driverOrderList.get(0);
        int user_id = order.getUser_id();
        where = " where is_enable = 1 and user_id = " + user_id;
        List<CrossCity> driverInfoList = appDB.getCrossCityList(where);
        where = " where _id =" + user_id;
        User user = appDB.getUserList(where).get(0);
        if (passengerOrderList.size() > 0 && driverInfoList.size() == 0) {
            PassengerOrder passengerOrder = passengerOrderList.get(0);
            if (user.getIs_car_owner() == 1) {
                DepartureInfo departure = new DepartureInfo();
                departure.setUser_id(user.getUser_id());
                departure.setMobile(user.getUser_mobile());
                departure.setStart_time(passengerOrder.getDeparture_time());
                departure.setBoarding_point(passengerOrder.getBoarding_point());
                departure.setBreakout_point(passengerOrder.getBreakout_point());
                departure.setInit_seats(5);
                departure.setCurrent_seats(5 - passengerOrder.getSeats());
                departure.setDeparture_city_code(passengerOrder.getDeparture_city_code());
                departure.setDeparture_address_code(passengerOrder.getDeparture_address_code());
                departure.setDestination_city_code(passengerOrder.getDestination_city_code());
                departure.setDestination_address_code(passengerOrder.getDestination_address_code());
                departure.setPrice(passengerOrder.getPay_money());
                departure.setRemark(passengerOrder.getRemark());
                departure.setBoarding_latitude(passengerOrder.getBoarding_latitude());
                departure.setBoarding_longitude(passengerOrder.getBoarding_longitude());
                departure.setBreakout_latitude(passengerOrder.getBreakout_latitude());
                departure.setBreakout_longitude(passengerOrder.getBreakout_longitude());
                departure.setDeparture_code(passengerOrder.getDeparture_code());
                departure.setDestination_code(passengerOrder.getDestination_code());
                appDB.createPCHDeparture(departure, source);
            }
        }
    }
}
