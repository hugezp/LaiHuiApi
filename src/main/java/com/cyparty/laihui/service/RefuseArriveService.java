package com.cyparty.laihui.service;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.ApiDB;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.ConfigUtils;
import com.cyparty.laihui.utilities.NotifyPush;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by pangzhenpeng on 2017/6/19.
 */

public class RefuseArriveService {
    @Autowired
    static NotifyPush notifyPush;

    @Transactional(readOnly = false)
    public static String getRefuseArrive(AppDB appDB, ApiDB apiDB, HttpServletRequest request) {
        JSONObject result = new JSONObject();
        String json = "";
        try {
            int passengerId = Integer.parseInt(request.getParameter("user_id"));
            if (request.getParameter("token") != null && request.getParameter("token").length() == 32) {
                int driverId = appDB.getIDByToken(request.getParameter("token"));
                String where = " where _id =" + driverId;
                String mobile = appDB.getUserList(where).get(0).getUser_mobile();
                where = " SET is_del=0 WHERE driver_phone='" + mobile + "' and passenger_id = " + passengerId;
                //修改状态
                boolean isSuccess = apiDB.update("arrive_driver_relation", where);

                if (isSuccess) {
                    json = AppJsonUtils.returnSuccessJsonString(result, "拒绝成功!");
                    return json;
                } else {
                    result.put("error_code", ErrorCode.ERROR_SYSTEM);
                    json = AppJsonUtils.returnFailJsonString(result, "服务器错误!");
                    return json;
                }
            } else {
                result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                json = AppJsonUtils.returnFailJsonString(result, "非法token!");
                return json;
            }
        } catch (Exception e) {
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误!");
            return json;
        }
    }


    public static String getSnatchArrive(AppDB appDB, ApiDB apiDB, HttpServletRequest request)throws Exception {
        JSONObject result = new JSONObject();
        User user = new User();
        boolean isSuccess = false;
        String json = "";
        //乘客车单ID
        int passengerCarId = Integer.parseInt(request.getParameter("car_id"));
        if (request.getParameter("token") != null && request.getParameter("token").length() == 32) {
            int userId = appDB.getIDByToken(request.getParameter("token"));
            if (userId > 0) {
                String where = " where _id=" + userId;
                user = appDB.getUserList(where).get(0);
                int source = 0;
                String nowSource = request.getParameter("source");
                if (nowSource != null && !nowSource.isEmpty() && nowSource.equals("iOS")) {
                    source = 1;
                }
                //判断该单是否已经被抢
                String passenger_order_where = " a left join pc_user b on a.user_id=b._id where order_id=" + passengerCarId + " and is_enable=1 and order_type = 0 and order_status= 200";
                //检测当前乘客行程是否依旧有效
                List<Order> passengerOrderList = appDB.getOrderReview(passenger_order_where, 1);
                if (passengerOrderList.size() > 0) {
                    Order passengerOrder = passengerOrderList.get(0);
                    if (passengerOrder.getUser_id() == userId) {
                        result.put("error_code", ErrorCode.INVITE_TIME);
                        json = AppJsonUtils.returnFailJsonString(result, "抱歉，不能抢自己发的车单哦！");
                        return json;
                    }
                    //判断车主座位数是否满足当前乘客车单的需要
                    String snatchTime = Utils.getCurrentTime();//抢单时间
                    String departure_time = Utils.getCurrentTime().split(" ")[0];
                    String order_where = " where  user_id= " + userId + " and create_time >= '" + departure_time + " 00:00:00'  and create_time <='" + departure_time + " 24:00:00' and order_type=2 ";
                    int total = appDB.getCount("pc_orders", order_where);
                    //规定每天可以预定5次
                    if (total <= ConfigUtils.DRIVER_GRAD_ORDER_COUNTS) {
                        Order order = new Order();
                        order.setUser_id(userId);
                        order.setOrder_id(passengerOrder.getOrder_id());
                        order.setSource(source);
                        order.setOrder_status(100);
                        order.setOrder_type(2);
                        order.setCreate_time(snatchTime);
                        order.setIs_enable(1);
                        isSuccess = appDB.createOrderReview(order);
                    } else {
                        result.put("error_code", ErrorCode.BOOKING_TIMES_LIMIT);
                        json = AppJsonUtils.returnFailJsonString(result, "每日抢单次数达到上限！");
                        return json;
                    }
                    if (isSuccess) {
                        //更改乘客行程单状态
                        String update_sql = " set order_status=100 , update_time='" + Utils.getCurrentTime() + "' where _id=" + passengerOrder.get_id();
                        boolean is = appDB.update("pc_orders", update_sql);
                        //设置乘客车单状态
                        update_sql = " set order_status= 1 where _id=" + passengerOrder.getOrder_id();
                        appDB.update("pc_passenger_publish_info", update_sql);
                        //通知乘客
                        String p_mobile = passengerOrder.getUser_mobile();
                        String driverMobile = user.getUser_mobile();
                        String content = "您的必达车单在" + snatchTime + "被尾号为" + driverMobile.substring(7, 11) + "的司机抢单，请您及时处理！";
                        where = " SET is_del=0 WHERE driver_phone='" + driverMobile + "' and passenger_id = " + passengerOrder.getUser_id();
                        //修改状态
                        apiDB.update("arrive_driver_relation", where);
                        PassengerOrder passengerPublishInfo = appDB.getPassengerDepartureInfo(" where a._id=" + passengerCarId + " and is_enable = 1").get(0);
                        //乘客信息，司机信息，乘客订单信息
                        JSONObject data = AppJsonUtils.getPushObject(appDB, passengerOrder, 2);
                        data.put("order_status", 100);
                        data.put("isArrive", 1);
                        data.put("boarding_point", passengerPublishInfo.getBoarding_point());
                        data.put("breakout_point", passengerPublishInfo.getBreakout_point());
                        data.put("departure_time", passengerPublishInfo.getDeparture_time());
                        data.put("seats", passengerPublishInfo.getSeats());
                        data.put("price", passengerPublishInfo.getPay_money());
                        data.put("record_id", passengerPublishInfo.get_id());
                        data.put("order_id", passengerOrder.get_id());

                        int push_id = userId;
                        int receive_id = passengerOrder.getUser_id();
                        int push_type = 11;
                        boolean is_true = appDB.createPush(passengerOrder.get_id(), push_id, receive_id, push_type, content, 11, "11.caf", data.toJSONString(), 1, driverMobile, null,1);
                        if (is_true) {
                            //将抢单信息通知给乘客
                            notifyPush.pinCheNotify("11", p_mobile, content, passengerOrder.get_id(), data, snatchTime);
                        }
                        where = " WHERE order_id = " + passengerOrder.getOrder_id();
                        int now_id = appDB.getMaxID("_id", "pc_orders", where);
                        //司机订单ID
                        result.put("driver_order_id", now_id);
                        json = AppJsonUtils.returnSuccessJsonString(result, "抢单成功！");
                        return json;
                    } else {
                        result.put("error_code", ErrorCode.ERROR_SYSTEM);
                        json = AppJsonUtils.returnFailJsonString(result, "服务器错误!");
                        return json;
                    }
                } else {
                    json = AppJsonUtils.returnFailJsonString(result, "下手晚了，该单已被其他车主抢走了！");
                    return json;
                }
            } else {
                result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                json = AppJsonUtils.returnFailJsonString(result, "非法token!");
                return json;
            }
        } else {
            result.put("error_code", ErrorCode.TOKEN_EXPIRED);
            json = AppJsonUtils.returnFailJsonString(result, "非法token!");
            return json;
        }

    }
}
