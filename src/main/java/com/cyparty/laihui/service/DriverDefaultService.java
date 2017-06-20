package com.cyparty.laihui.service;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.NotifyPush;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 必达单车主抢单默认生成车单
 * Created by YangGuang on 2017/6/20.
 */
public class DriverDefaultService {

    @Transactional(readOnly = false)
    public static String driverDefault(AppDB appDB, HttpServletRequest request) {
        JSONObject result = new JSONObject();
        String json = "";
        String confirm_time = Utils.getCurrentTime();
        //判断用户标识
        String token = request.getParameter("token");
        int source = 0;
        String now_source = request.getParameter("source");
        if (now_source != null && !now_source.isEmpty() && now_source.equals("iOS")) {
            source = 1;
        }
        try {
            if (token != null && token.length() == 32) {
                int car_id = Integer.parseInt(request.getParameter("car_id"));
                String where = " where a._id = " + car_id;
                List<PassengerOrder> passengerOrderList = appDB.getPassengerDepartureInfo(where);
                if (passengerOrderList.size()>0){
                    PassengerOrder passengerOrder = passengerOrderList.get(0);
                    String uwhere = " where _id =" + appDB.getIDByToken(token);
                    User user = appDB.getUserList(uwhere).get(0);
                    if (user.getIs_car_owner() == 1){
                        DepartureInfo departure = new DepartureInfo();
                        departure.setUser_id(user.getUser_id());
                        departure.setMobile(user.getUser_mobile());
                        departure.setStart_time(passengerOrder.getDeparture_time());
                        departure.setBoarding_point(passengerOrder.getBoarding_point());
                        departure.setBreakout_point(passengerOrder.getBreakout_point());
                        departure.setInit_seats(5);
                        departure.setCurrent_seats(5);
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
                    json = AppJsonUtils.returnSuccessJsonString(result, "创建成功！");
                }else {
                    result.put("error_code", ErrorCode.getParameter_wrong());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                }
                return json;
            } else {
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
}
