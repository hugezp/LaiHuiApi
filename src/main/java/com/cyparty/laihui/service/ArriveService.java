package com.cyparty.laihui.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.ArriveDriver;
import com.cyparty.laihui.domain.PassengerOrder;
import com.cyparty.laihui.utilities.DateUtils;
import com.cyparty.laihui.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 必达单车主service
 * Created by pangzhenpeng on 2017/6/16.
 */
public class ArriveService {

    public static JSONObject getArriveListJson(String mobile, AppDB appDB, int page, int size) {
        JSONObject resultJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String where = " WHERE driver_phone = '" + mobile + "'AND is_del = 1";
        int count = 0;
        List<ArriveDriver> arriveDriverList = appDB.getArriveList(where);
        List<PassengerOrder> passengerOrderList = new ArrayList<>();
        if (arriveDriverList.size() > 0) {
            StringBuilder orderNos = new StringBuilder();
            orderNos.append("(");
            for (int i = 0; i < arriveDriverList.size(); i++) {
                orderNos.append(arriveDriverList.get(i).getOrderNo());
                if (i != arriveDriverList.size() - 1) {
                    orderNos.append(",");
                }
            }
            orderNos.append(")");
            String where1 = " and is_enable =1 and departure_time >'" + Utils.getCurrentTime() + "' and trade_no in " + orderNos + " order by convert (departure_time USING gbk)COLLATE gbk_chinese_ci asc";
            passengerOrderList = appDB.getPassengerList(where1);
            count = passengerOrderList.size();
        }

        if (passengerOrderList.size() > 0) {
            for (PassengerOrder passenger : passengerOrderList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("car_id", passenger.get_id());
                jsonObject.put("mobile", passenger.getMobile());
                jsonObject.put("departure_time", DateUtils.getProcessdTime(passenger.getDeparture_time()));
                jsonObject.put("create_time", DateUtils.getTimesToNow(passenger.getCreate_time()));
                jsonObject.put("price", passenger.getPay_money());
                jsonObject.put("i_province", net.sf.json.JSONObject.fromObject(passenger.getBoarding_point()).get("province"));
                //出发城市
                jsonObject.put("i_city", net.sf.json.JSONObject.fromObject(passenger.getBoarding_point()).get("city"));
                //出发地点
                String id = net.sf.json.JSONObject.fromObject(passenger.getBoarding_point()).get("id").toString();
                jsonObject.put("i_name", net.sf.json.JSONObject.fromObject(passenger.getBoarding_point()).get("name"));
                jsonObject.put("o_province", net.sf.json.JSONObject.fromObject(passenger.getBreakout_point()).get("province"));
                jsonObject.put("o_city", net.sf.json.JSONObject.fromObject(passenger.getBreakout_point()).get("city"));
                jsonObject.put("o_name", net.sf.json.JSONObject.fromObject(passenger.getBreakout_point()).get("name"));
                if (passenger.getUser_name() == null) {
                    jsonObject.put("name", "");
                } else {
                    jsonObject.put("name", passenger.getUser_name());
                }
                jsonObject.put("user_avatar", passenger.getUser_avatar());
                jsonObject.put("user_id", passenger.getUser_id());
                jsonObject.put("booking_seats", passenger.getSeats());
                jsonObject.put("remark", passenger.getRemark());
                jsonArray.add(jsonObject);
            }
        }
        resultJson.put("passenger_data", jsonArray);
        resultJson.put("page", page);
        resultJson.put("count", count);
        resultJson.put("size", size);
        return resultJson;
    }

}

