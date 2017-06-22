package com.cyparty.laihui.service;


import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.DateUtils;
import com.cyparty.laihui.utilities.NotifyPush;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * Created by pangzhenpeng on 2017/6/20.
 */
public class PassengerArriveService {
    @Autowired
    static NotifyPush notifyPush;
    private static String json = "";

    public static String getInviteDriver(HttpServletRequest request, AppDB appDB) {
        JSONObject result = new JSONObject();
        User user = new User();
        int userId = 0;
        try {
            String token = request.getParameter("token");
            if (token != null && token.length() == 32) {
                userId = appDB.getIDByToken(token);
                try {
                    List<User> userList = appDB.getUserList(" where _id=" + userId);
                    if (userList.size() > 0) {
                        user = userList.get(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                }
                int flag = 1;
                String where = " where user_id = " + userId + " and order_type=0 and order_status<=300 and order_status>=100 and is_enable=1";
                List<Order> orderList1 = appDB.getOrderReview(where, 0);
                if (orderList1.size() == 0) {
                    //乘客没有创建车单标记
                    flag = 0;
                    result.put("flag", flag);
                    json = AppJsonUtils.returnFailJsonString(result, "请您创建行程后再邀请车主");
                }
                result.put("flag", flag);
                boolean is_enable = false;
                if (userId > 0) {
                    //获取系统现在时间
                    String confirm_time = Utils.getCurrentTime();
                    where = " where user_id =" + userId + " and is_enable =1 order by CONVERT (create_time USING gbk)COLLATE gbk_chinese_ci desc limit 1";
                    Order passenger = appDB.getOrderReview(where, 0).get(0);
                    //获取车主手机号
                    String driverMobile = request.getParameter("driverMobile");
                    //获取车主车单id
                    int driverCarId = Integer.parseInt(request.getParameter("driverCarId"));
                    where = " where a._id =" + driverCarId;
                    int driver_user_id = appDB.getAppDriverDpartureInfo(where).get(0).getUser_id();
                    //乘客车单id
                    int grab_id = passenger.getOrder_id();
                    String content = "乘客" + user.getUser_nick_name() + "在" + confirm_time + "邀请您抢单！";
                    JSONObject passengerData = new JSONObject();
                    passengerData.put("user_avatar", passenger.getUser_avatar());
                    passengerData.put("user_name", passenger.getUser_name());
                    passengerData.put("boarding_point", passenger.getBoarding_point());
                    passengerData.put("breakout_point", passenger.getBreakout_point());
                    passengerData.put("remark", passenger.getRemark());
                    passengerData.put("departure", passenger.getDeparture_time());
                    passengerData.put("create_time", passenger.getCreate_time());
                    passengerData.put("mobile", passenger.getUser_mobile());
                    passengerData.put("order_status", passenger.getOrder_status());
                    passengerData.put("price", passenger.getPrice());
                    passengerData.put("isArrive", passenger.getIsArrive());
                    PCCount driverPCCount = AppJsonUtils.getPCCount(appDB, passenger.getUser_id());
                    passengerData.put("pc_count", driverPCCount.getTotal());
                    where = " where passenger_car_id=" + grab_id + " and driver_car_id=" + driverCarId;
                    //获取邀请记录
                    List<InviteIimit> inviteIimit = appDB.getinviteIimit(where);
                    if (inviteIimit.size() > 0) {
                        //查询上次邀请距离这次邀请的时间
                        if (DateUtils.getTimesToNow1(inviteIimit.get(0).getInvite_time()) > 15) {
                            where = " set invite_time='" + confirm_time + "' where passenger_car_id=" + grab_id + " and driver_car_id=" + driverCarId;
                            appDB.update("pc_invite_limit", where);
                            is_enable = true;
                        }
                    } else {
                        //添加邀请记录
                        double price;
                        try {
                            price = Double.parseDouble(request.getParameter("price"));
                        } catch (Exception e) {
                            price = 0.0;
                        }
                        appDB.createInviteIimit(grab_id, userId, driver_user_id, confirm_time, price, driverCarId);
                        is_enable = true;
                    }
                    if (is_enable) {
                        //保存推送消息
                        int push_id = userId;
                        int receive_id = driver_user_id;
                        int push_type = 28;
                        appDB.createPush(grab_id, push_id, receive_id, push_type, content, 28, "28.caf", passengerData.toJSONString(), 1, user.getUser_nick_name(), null);
                        //将邀请消息推送给车主
                        notifyPush.pinCheNotify("28", driverMobile, content, grab_id, passengerData, confirm_time);
                        result.put("passengerData",passengerData);
                        json = AppJsonUtils.returnSuccessJsonString(result, "您已成功邀请车主抢单！");
                    } else {
                        json = AppJsonUtils.returnFailJsonString(result, "邀请过于频繁，请稍后再试！");
                    }
                }
            }
        } catch (Exception e) {
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
        }

        return json;
    }

    /**
     * 乘客订单详情
     */
    public static String orderDetail(AppDB appDB,HttpServletRequest request){
        JSONObject result = new JSONObject();
        String json = "";
        String confirm_time = Utils.getCurrentTime();
        //乘客订单记录id
        int orderId = Integer.parseInt(request.getParameter("order_id"));
        //判断用户标识
        String token = request.getParameter("token");
        if (token != null && token.length() == 32) {
            result = AppJsonUtils.getMyArriveBookingOrderInfo(appDB, orderId);
            json = AppJsonUtils.returnSuccessJsonString(result, "请求成功！");
            return json;
        }else {
            result.put("error_code", ErrorCode.getToken_expired());
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return json;
        }
    }
}