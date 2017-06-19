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
    public static String passengerStatus(HttpServletRequest request, AppDB appDB){
        JSONObject result = new JSONObject();
        JSONObject driverResult = new JSONObject();
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
                if (orderList.size()>0){
                    Order order = orderList.get(0);
                    int status = order.getOrder_status();
                    result.put("status",status);
                    result.put("isArrive",order.getIsArrive());
                    //得到司机基本信息
                    List<DepartureInfo> departures = appDB.getAppDriverDpartureInfo("where a.is_enable=1 and a.user_id=" + order.getDriver_id() + " order by CAST(a.create_time AS time) DESC limit 1");
                    if (departures.size() > 0) {
                        if (departures.get(0).getCurrent_seats() == 0) {
                            //车单状态
                            result.put("status", "4");
                            //车单状态备注
                            result.put("remake", "该车辆已经没有座位，请换乘其他车辆");
                        }
                        DepartureInfo departure = departures.get(0);
                        result.put("boarding_point", JSONObject.parseObject(departure.getBoarding_point()));
                        result.put("breakout_point", JSONObject.parseObject(departure.getBreakout_point()));
                        result.put("description", departure.getDescription());
                        result.put("create_time", departure.getCreate_time());
                        result.put("departure_time", departure.getStart_time());
                        result.put("price", departure.getPrice());
                        result.put("current_seats", departures.get(0).getCurrent_seats());
                        result.put("init_seats", departures.get(0).getInit_seats());
                        result.put("car_id", departures.get(0).getR_id());
                        List<CarOwnerInfo> carOwnerInfos = appDB.getCarOwnerInfo("where a.user_id=" + order.getDriver_id());
                        if (carOwnerInfos.size() > 0) {
                            CarOwnerInfo user1 = carOwnerInfos.get(0);
                            if (user1.getFlag() == 0) {
                                driverResult.put("car", user1.getCar_id());
                                driverResult.put("car_brand", user1.getCar_brand());
                                driverResult.put("car_color", user1.getCar_color());
                                driverResult.put("car_type", user1.getCar_type());
                            } else {
                                List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(order.getDriver_id());
                                UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                                driverResult.put("car", travelCardInfo.getCar_license_number());
                                if (travelCardInfos.size() > 0) {
                                    driverResult.put("car_brand", "");
                                    driverResult.put("car_color", travelCardInfo.getCar_color());
                                    driverResult.put("car_type", travelCardInfo.getCar_type());
                                }
                            }
                            driverResult.put("car_owner", user1.getCar_owner());
                        } else {
                            List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(order.getDriver_id());
                            UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                            driverResult.put("car", travelCardInfo.getCar_license_number());
                            if (travelCardInfos.size() > 0) {
                                driverResult.put("car_brand", "");
                                driverResult.put("car_color", travelCardInfo.getCar_color());
                                driverResult.put("car_type", travelCardInfo.getCar_type());
                            }
                        }
                        where = " where _id=" + order.getDriver_id();
                        List<User> drivers = appDB.getUserList(where);
                        if (drivers.size() > 0) {
                            User driver = drivers.get(0);
                            driverResult.put("mobile", driver.getUser_mobile());
                            driverResult.put("name", driver.getUser_nick_name());
                            driverResult.put("avatar", driver.getAvatar());

                            PCCount driverPCCount = getPCCount(appDB, driver.getUser_id());
                            driverResult.put("pc_count", driverPCCount.getTotal());
                        }
                        result.put("driver_data",driverResult);
                        json = AppJsonUtils.returnSuccessJsonString(result,"车主列表获取成功！");
                        return json;
                    }else {
                        result.put("driver_data",driverResult);
                        json = AppJsonUtils.returnSuccessJsonString(result, "暂无车辆信息！");
                        return json;
                    }
                }else {
                    result.put("status",0);
                    result.put("error_code",ErrorCode.getNo_data());
                    json = AppJsonUtils.returnFailJsonString(result, "暂无订单数据！");
                    return json;
                }
            }else {
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return json;
            }
        }catch (Exception e){
            result.put("error_code", ErrorCode.getError_system());
            json = AppJsonUtils.returnFailJsonString(result, "服务器错误！");
            return json;
        }
    }

    public static PCCount getPCCount(AppDB appDB, int user_id) {
        PCCount pcCount = new PCCount();
        //统计司机发布全部拼车次数
        String where = " where user_id =" + user_id + " and is_enable=1";
        int driver_departure_total = appDB.getCount("pc_driver_publish_info", where);//司机发车次数

        String where_count = where + " and order_status =3 and order_type=2";
        int booking_total = appDB.getCount("pc_orders", where_count);//司机订单次数
        where_count = where + " and order_status =4 and order_type=0";
        int passenger_total = appDB.getCount("pc_orders", where_count);//订单次数

        pcCount.setDriver_departure_count(driver_departure_total);

        pcCount.setPassenger_booking_count(booking_total);


        pcCount.setTotal(driver_departure_total + booking_total + passenger_total);

        return pcCount;
    }
}
