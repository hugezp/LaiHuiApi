package com.cyparty.laihui.service;


import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import com.cyparty.laihui.utilities.*;
import org.jsoup.helper.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.cyparty.laihui.controller.PayOrderController.buildOrderParam;
import static com.cyparty.laihui.controller.PayOrderController.getSign;
import static com.cyparty.laihui.controller.PayOrderController.senPost;


/**
 * Created by pangzhenpeng on 2017/6/20.
 */
public class PassengerArriveService {
    @Autowired
    static NotifyPush notifyPush;

    public static String getInviteDriver(HttpServletRequest request, AppDB appDB) {
        JSONObject result = new JSONObject();
        User user = new User();
        String json = "";
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
                    result.put("error_code", ErrorCode.TOKEN_EXPIRED);
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
                        result.put("passengerData", passengerData);
                        json = AppJsonUtils.returnSuccessJsonString(result, "您已成功邀请车主抢单！");
                    } else {
                        json = AppJsonUtils.returnFailJsonString(result, "邀请过于频繁，请稍后再试！");
                    }
                }
            }
        } catch (Exception e) {
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
        }

        return json;
    }

    /**
     * 乘客订单详情
     */
    public static String orderDetail(AppDB appDB, HttpServletRequest request) {
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
        } else {
            result.put("error_code", ErrorCode.TOKEN_EXPIRED);
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return json;
        }
    }

    public static String insertItinerary(AppDB appDB, HttpServletRequest request) {
        JSONObject result = new JSONObject();
        String json = "";
        String token = null;
        boolean is_success = true;
        int seats = 0;
        int user_id = 0;
        if (request.getParameter("token") != null) {
            try {
                token = request.getParameter("token");
                user_id = appDB.getIDByToken(token);
            } catch (Exception e) {
                user_id = 0;
            }
        }
        if (ParamVerificationUtils.insertItineraryValidation(request) == 0) {
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return json;
        }
        String boarding_point = request.getParameter("boarding_point");
        String breakout_point = request.getParameter("breakout_point");
        int departure_address_code = 0;
        int departure_city_code = 0;
        int destination_address_code = 0;
        int destination_city_code = 0;
        String boarding_latitude = "";
        String boarding_longitude = "";
        String breakout_latitude = "";
        String breakout_longitude = "";
        int departure_code = 0;
        int destination_code = 0;
        try {
            if (request.getParameter("booking_seats") != null) {
                seats = Integer.parseInt(request.getParameter("booking_seats"));
            }
        } catch (NumberFormatException e) {
            seats = 0;
            user_id = 0;
            e.printStackTrace();
        }
        if (user_id > 0) {
            String user_where = " where _id=" + user_id;
            User user = appDB.getUserList(user_where).get(0);
            if (user.getIs_validated() == 1) {
                String start_time = request.getParameter("departure_time");//出发时间
                //验证出发时间 周一到周六 早九点到晚六点
                String release_time = Utils.getCurrentTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                try {
                    Date startTime = format.parse(start_time);
                    Date releaseTime = format.parse(release_time);
                    long longTime = startTime.getTime() - releaseTime.getTime();
                    long hour = longTime / (60 * 60 * 1000);
                    if(hour<2){
                        json = AppJsonUtils.returnFailJsonString(result, "必达单必须提前两小时发布！");
                        return json;
                    }
                    c.setTime(format.parse(start_time));
                } catch (Exception e) {
                    e.printStackTrace();
                    result.put("error_code", ErrorCode.PARAMETER_WRONG);
                    json = AppJsonUtils.returnFailJsonString(result, "创建失败！");
                    return json;
                }
                int dayForWeek = 0;
                if(c.get(Calendar.DAY_OF_WEEK) == 1){
                    dayForWeek = 7;
                }else{
                    dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
                }
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);          //获取当前分钟
                int ss = c.get(Calendar.SECOND);          //获取当前秒
                if((dayForWeek==7)||(hour<11 || hour>18 || (hour==18 && (min>0 || ss>0)))){
                    json = AppJsonUtils.returnFailJsonString(result, "必达单出发时间必须为周一至周六早11点到晚6点！");
                    return json;
                }

                int source = 0;
                if (request.getParameter("source") != null && request.getParameter("source").equals("iOS")) {
                    source = 1;
                }
                String remark = "乘客轻装简行";
                if (request.getParameter("remark") != null && !request.getParameter("remark").isEmpty()) {
                    remark = request.getParameter("remark");
                }
                double price = 0;
                try {
                    price = Double.parseDouble(request.getParameter("price"));//价格
                } catch (NumberFormatException e) {
                    price = 0;
                    e.printStackTrace();
                }
                String description = request.getParameter("description");
                try {
                    JSONObject boardingObject = JSONObject.parseObject(boarding_point);
                    departure_address_code = boardingObject.getIntValue("adCode");
                    //验证出发地点 （郑州市）
                    String departure_address_code_str = String.valueOf(departure_address_code);
                    if(departure_address_code_str==null || departure_address_code_str.equals("")||departure_address_code_str.length()<6){
                        json = AppJsonUtils.returnFailJsonString(result, "获取参数错误！");
                        return json;
                    }else{
                        departure_address_code_str=departure_address_code_str.substring(0,4);
                        if(!departure_address_code_str.equals("4101")){
                            json = AppJsonUtils.returnFailJsonString(result, "出发地必须是郑州，其它地区暂未开通服务！");
                            return json;
                        }
                    }
                    departure_city_code = Integer.parseInt((departure_address_code + "").substring(0, 4) + "00");
                    boarding_latitude = boardingObject.getString("latitude");
                    boarding_longitude = boardingObject.getString("longitude");
                    departure_code = Integer.parseInt((departure_address_code + "").substring(0, 4));
                    JSONObject breakoutObject = JSONObject.parseObject(breakout_point);
                    destination_address_code = breakoutObject.getIntValue("adCode");
                    //验证目的地点 （河南省内）
                    String destination_address_code_str = String.valueOf(destination_address_code);
                    if(destination_address_code_str==null || destination_address_code_str.equals("")||destination_address_code_str.length()<6){
                        json = AppJsonUtils.returnFailJsonString(result, "获取参数错误！");
                        return json;
                    }else{
                        destination_address_code_str=destination_address_code_str.substring(0,2);
                        if(!destination_address_code_str.equals("41")){
                            json = AppJsonUtils.returnFailJsonString(result, "目的地必须是河南省内，其它地区暂未开通服务！");
                            return json;
                        }
                    }
                    destination_city_code = Integer.parseInt((destination_address_code + "").substring(0, 4) + "00");
                    breakout_latitude = breakoutObject.getString("latitude");
                    breakout_longitude = breakoutObject.getString("longitude");
                    destination_code = Integer.parseInt((destination_address_code + "").substring(0, 4));
                } catch (Exception e) {
                    e.printStackTrace();
                    result.put("error_code", ErrorCode.PARAMETER_WRONG);
                    json = AppJsonUtils.returnFailJsonString(result, "创建失败！");
                    return json;
                }
                String trade_no = Utils.getTimestamp() + Utils.random(2);//生成订单号
                PassengerOrder order = new PassengerOrder();
                order.setIsArrive(1);
                order.setPay_money(price);
                order.setSource(source);
                order.setUser_id(user_id);
                order.setStart_time(start_time);
                order.setSeats(seats);
                order.setBoarding_point(boarding_point);
                order.setDeparture_city_code(departure_city_code);
                order.setDeparture_address_code(departure_address_code);
                order.setBreakout_point(breakout_point);
                order.setDestination_city_code(destination_city_code);
                order.setDestination_address_code(destination_address_code);
                order.setDescription(description);
                order.setOrder_type(0);
                order.setRemark(remark);
                order.setBoarding_latitude(boarding_latitude);
                order.setBoarding_longitude(boarding_longitude);
                order.setBreakout_latitude(breakout_latitude);
                order.setBreakout_longitude(breakout_longitude);
                order.setDeparture_code(departure_code);
                order.setDestination_code(destination_code);
                order.setPay_num(trade_no);
                order.setIs_enable(0);
                //判断是否已存在进行中车单
                String order_where = " a right join pc_passenger_publish_info b on a.order_id=b._id where  b.is_enable=1 and ((a.order_status<4 and a.order_status>=0) or a.order_status > 20) and order_type=0 and a.user_id=" + user_id;
                List<Order> passengerDepartureInfo = appDB.getOrderReview(order_where, 2);
                if (passengerDepartureInfo.size() > 0) {
                    json = AppJsonUtils.returnFailJsonString(result, "太贪心了，您尚有未完成的行程！");
                    return json;
                } else {
                    //查询今日已发布次数
                    String now_time = Utils.getCurrentTime().split(" ")[0] + " 00:00:00";
                    String now_where = "";
                    now_where = " where user_id=" + user_id + " and create_time >='" + now_time + "' and order_type=0 ";
                    List<Order> todayOrderList = appDB.getOrderReview(now_where, 0);
                    if (todayOrderList.size() < ConfigUtils.DRIVER_DEPARTURE_COUNTS) {
                        //将车单添加到数据库中
                        is_success = appDB.createPassengerDepartureArrive(order);
                    } else {
                        json = AppJsonUtils.returnFailJsonString(result, "每日发布行程次数为" + ConfigUtils.PASSENGER_DEPARTURE_COUNTS + "次，您今日发布次数已达到上限！");
                        return json;
                    }
                }
                if (is_success) {
                    //记录乘客发布
                    String confirm_time = Utils.getCurrentTime();
                    //乘客车单ID
                    int id = appDB.getMaxID("_id", "pc_passenger_publish_info");
                    Order order1 = new Order();
                    order1.setUser_id(user_id);
                    order1.setOrder_id(id);
                    order1.setSource(source);
                    order1.setOrder_status(200);
                    order1.setOrder_type(0);
                    order1.setIs_complete(0);
                    order1.setCreate_time(confirm_time);
                    order1.setRemark(remark);
                    order1.setIs_enable(0);
                    appDB.createOrderReview(order1);
                    //乘客车单ID
                    List<Order> orders = appDB.getOrderReview(" where order_id =" + id, 0);
                    result.put("car_id", id);
                    result.put("order_id", orders.get(0).get_id());
                    result.put("boarding_point", boarding_point);
                    result.put("breakout_point", breakout_point);
                    result.put("departure_time", start_time);
                    result.put("price", order.getPay_money());
                    result.put("isArrive", 1);
                    result.put("tradeNo", trade_no);
                    result.put("confirmTime", confirm_time);
                    result.put("msg", "成功！");
                    json = AppJsonUtils.returnSuccessJsonString(result, "行程创建成功！");
                    return json;
                }
                json = AppJsonUtils.returnFailJsonString(result, "行程创建失败！");
                return json;
            } else {
                result.put("error_code", ErrorCode.IS_VALIDATED);
                json = AppJsonUtils.returnFailJsonString(result, "请先进行乘客身份认证！");
                return json;
            }
        }
        return json;
    }

    public static String judgment(AppDB appDB, HttpServletRequest request) throws Exception {
        JSONObject result = new JSONObject();
        String json = "";
        String where = "";
        boolean success = false;
        String tradeNo = request.getParameter("tradeNo");
        String releaseTime = request.getParameter("releaseTime");
        if (ParamVerificationUtils.judgmentValidation(request) == 0) {
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return json;
        }
        if (DateUtils.getTimesToNow1(releaseTime)>ConfigUtils.ORDER_TIMEOUT){
            result.put("error_code", ErrorCode.ORDER_TIMEOUT);
            json = AppJsonUtils.returnFailJsonString(result, "订单支付超时");
            return json;
        }
        if (request.getParameter("flag").equals("1")) {
            success = true;
        }
        if (success) {
            where = " set is_enable= 1 where trade_no = '" + tradeNo + "'";
            appDB.update("pc_passenger_publish_info", where);
            where = " a right join pc_passenger_publish_info b on a.order_id=b._id where trade_no='"+tradeNo+"'";
            List<Order> orderList = appDB.getOrderReview(where, 2);
            if (orderList.size() > 0) {
                where = " set is_enable= 1 where _id = " + orderList.get(0).get_id() + "";
                appDB.update("pc_orders", where);
                json = AppJsonUtils.returnSuccessJsonString(result, "行程创建成功！");
                return json;
            }
        }
        json = AppJsonUtils.returnFailJsonString(result, "行程创建失败！");
        return json;
    }
}