package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.DriverAndCar;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.domain.PassengerOrder;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.RangeUtils;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/4.
 */
@Controller
@ResponseBody
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class NearByOwnerOrPassengerController {

    @Autowired
    AppDB appDB;

    /**
     * 首页模块
     *
     * @param request
     * @return 首页所需数据
     */
    @ResponseBody
    @RequestMapping(value = "/nearby/OwnerOrPassenger", method = RequestMethod.POST)
    public ResponseEntity<String> search(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        //附近车主条数
        int count = 0;
        //用户id
        int user_id = 0;
        String json = "";
        String where = null;
        //获取系统时间
        String current_time = Utils.getCurrentTime();
        //附近车主（乘客）搜索范围
        double query_distance = 50000.0;
        try {
            String action = request.getParameter("action");
            int page = 0;
            int size = 10;
            if (request.getParameter("page") != null && !request.getParameter("page").trim().equals("")) {
                try {
                    page = Integer.parseInt(request.getParameter("page"));
                } catch (NumberFormatException e) {
                    page = 0;
                    e.printStackTrace();
                }
            }
            if (request.getParameter("size") != null && !request.getParameter("size").trim().equals("")) {
                try {
                    size = Integer.parseInt(request.getParameter("size"));
                } catch (NumberFormatException e) {
                    size = 10;
                    e.printStackTrace();
                }
            }
            //获取用户token值
            String token = request.getParameter("token");
            if (token != null && token.length() == 32) {
                user_id = appDB.getIDByToken(token);

            } else {
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
            switch (action) {
                case "nearby_owner":
                    //获取乘客经度
                    double p_longitude = Double.parseDouble(request.getParameter("p_longitude"));
                    //获取乘客纬度
                    double p_latitude = Double.parseDouble(request.getParameter("p_latitude"));
                    //用户与车主（乘客）的距离
                    double distance = 0.0;
                    //符合SQL条件搜索要求的车主列表
                    List<DriverAndCar> owenrList = null;
                    //乘客附近的车主列表
                    List<DriverAndCar> nearByOwenrList1 = new ArrayList();
                    List<DriverAndCar> nearByOwenrList2 = new ArrayList();
                    where = " and is_enable =1 and departure_time>'" + current_time + "' order by CONVERT (departure_time USING gbk)COLLATE gbk_chinese_ci desc";
                    owenrList = appDB.getOwenrList(where);
                    for (int i = 0; i < owenrList.size(); i++) {
                        //移除本用户的车单
                        if (owenrList.get(i).getUser_id() == user_id) {
                            owenrList.remove(i);
                            i--;
                            continue;
                        }
                        //移除剩余位置为0的车单
                        if (owenrList.get(i).getCurrent_seats() == 0) {
                            owenrList.remove(i);
                            i--;
                            continue;
                        }
                        String address_board4DB = owenrList.get(i).getBoarding_point();
                        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(address_board4DB);
                        double o_longitude = Double.parseDouble(jsonObject.get("longitude").toString());
                        double o_latitude = Double.parseDouble(jsonObject.get("latitude").toString());
                        //通过经纬度获取距离
                        distance = RangeUtils.getDistance(p_latitude, p_longitude, o_latitude, o_longitude);
                        if (distance <= query_distance) {
                            nearByOwenrList1.add(owenrList.get(i));
                        }
                    }
                    if (nearByOwenrList1.size() != 0) {
                        if (nearByOwenrList1.size() > 3) {
                            count = 3;
                            for (int i = 0; i < 3; i++) {
                                nearByOwenrList2.add(nearByOwenrList1.get(i));
                            }
                        } else {
                            count = nearByOwenrList1.size();
                            for (int i = 0; i < nearByOwenrList1.size(); i++) {
                                nearByOwenrList2.add(nearByOwenrList1.get(i));
                            }
                        }
                        result = AppJsonUtils.getNearByOwnerList(nearByOwenrList2, page, size, count);
                        result.put("active", AppJsonUtils.active(appDB));
                        result.put("order", AppJsonUtils.order(appDB, user_id, 0, "passenger"));
                        result.put("message", AppJsonUtils.isNewMessage(appDB, user_id));
                        result.put("route", AppJsonUtils.commonRoute(appDB, user_id, null));
                        json = AppJsonUtils.returnSuccessJsonString(result, "附近车主获取成功");
                    } else {
                        result = AppJsonUtils.getNearByOwnerList(nearByOwenrList2, page, size, count);
                        result.put("active", AppJsonUtils.active(appDB));
                        result.put("order", AppJsonUtils.order(appDB, user_id, 0, "passenger"));
                        result.put("message", AppJsonUtils.isNewMessage(appDB, user_id));
                        result.put("route", AppJsonUtils.commonRoute(appDB, user_id, null));
                        json = AppJsonUtils.returnSuccessJsonString(result, "您的附近暂时还没有车主出现哦");
                    }
                    break;
                case "nearby_passenger":
                    //获取车主经纬度
                    double o_lon = Double.parseDouble(request.getParameter("p_longitude"));
                    double o_lat = Double.parseDouble(request.getParameter("p_latitude"));
                    //符合要求的车主列表
                    List<PassengerOrder> passengerList = null;
                    //乘客附近的车主列表
                    List<PassengerOrder> nearByPassengerList = new ArrayList();
                    List<PassengerOrder> nearByPassengerList2 = new ArrayList();
                    where = " and is_enable =1 and departure_time >'" + current_time + "' order by CONVERT (departure_time USING gbk)COLLATE gbk_chinese_ci desc";
                    passengerList = appDB.getPassengerList1(where);
                    for (int i = 0; i < passengerList.size(); i++) {
                        if (passengerList.get(i).getUser_id() == user_id) {
                            passengerList.remove(i);
                            i--;
                            continue;
                        }
                        String address_board4DB = passengerList.get(i).getBoarding_point();
                        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(address_board4DB);
                        //乘客位置经纬度
                        double p_lon = Double.parseDouble(jsonObject.get("longitude").toString());
                        double p_lat = Double.parseDouble(jsonObject.get("latitude").toString());
                        //通过经纬度获取距离
                        distance = RangeUtils.getDistance(o_lat, o_lon, p_lat, p_lon);
                        if (distance <= query_distance) {
                            nearByPassengerList.add(passengerList.get(i));
                        }
                    }
                    if (nearByPassengerList.size() != 0) {
                        if (nearByPassengerList.size() > 3) {
                            count = 3;
                            for (int i = 0; i < 3; i++) {
                                nearByPassengerList2.add(nearByPassengerList.get(i));
                            }
                        } else {
                            count = nearByPassengerList.size();
                            for (int i = 0; i < nearByPassengerList.size(); i++) {
                                nearByPassengerList2.add(nearByPassengerList.get(i));
                            }
                        }
                        result = AppJsonUtils.getNearByPassengerList(nearByPassengerList2, page, size, count);
                        result.put("active", AppJsonUtils.active(appDB));
                        result.put("order", null);
                        result.put("route", AppJsonUtils.commonRoute(appDB, user_id, null));
                        result.put("publish", AppJsonUtils.order(appDB, user_id, 0, "owner"));
                        result.put("message", AppJsonUtils.isNewMessage(appDB, user_id));
                        json = AppJsonUtils.returnSuccessJsonString(result, "附近乘客获取成功！");
                    } else {
                        result = AppJsonUtils.getNearByPassengerList(nearByPassengerList2, page, size, count);
                        result.put("active", AppJsonUtils.active(appDB));
                        result.put("order", null);
                        result.put("route", AppJsonUtils.commonRoute(appDB, user_id, null));
                        result.put("publish", AppJsonUtils.order(appDB, user_id, 0, "owner"));
                        result.put("message", AppJsonUtils.isNewMessage(appDB, user_id));
                        json = AppJsonUtils.returnSuccessJsonString(result, "您的附近暂时还没有乘客出现哦！");
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }

    /**
     * 附近乘客（车主）列表模块
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/nearby/OwnerOrPassengerList", method = RequestMethod.POST)
    public ResponseEntity<String> search1(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        String where = null;
        int count = 0;
        int user_id = 0;
        //乘客与车主的距离
        double distance = 0.0;
        String current_time = Utils.getCurrentTime();
        //设定附近的定义范围
        double query_distance = 500000.0;
        try {
            String action = request.getParameter("action");
            int page = 0;
            int size = 10;
            if (request.getParameter("page") != null && !request.getParameter("page").trim().equals("")) {
                try {
                    page = Integer.parseInt(request.getParameter("page"));
                } catch (NumberFormatException e) {
                    page = 0;
                    e.printStackTrace();
                }
            }
            if (request.getParameter("size") != null && !request.getParameter("size").trim().equals("")) {
                try {
                    size = Integer.parseInt(request.getParameter("size"));
                } catch (NumberFormatException e) {
                    size = 10;
                    e.printStackTrace();
                }
            }
            String token = request.getParameter("token");
            if (token != null && token.length() == 32) {
                user_id = appDB.getIDByToken(token);

            } else {
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
            int offset = page * size;
            switch (action) {
                case "nearby_owner":
                    //获取乘客经纬度
                    double p_longitude = Double.parseDouble(request.getParameter("p_longitude"));
                    double p_latitude = Double.parseDouble(request.getParameter("p_latitude"));
                    //符合要求的车主列表
                    List<DriverAndCar> owenrList = null;
                    //乘客附近的车主列表
                    List<DriverAndCar> nearByOwenrList = new ArrayList();

                    //"latitude > '.$lat.'-1 and latitude < '.$lat.'+1 and longitude > '.$lon.'-1 and longitude < '.$lon.'+1 order by ACOS(SIN(('.$lat.' * 3.1415) / 180 ) *SIN((latitude * 3.1415) / 180 ) +COS(('.$lat.' * 3.1415) / 180 ) * COS((latitude * 3.1415) / 180 ) *COS(('.$lon.'* 3.1415) / 180 - (longitude * 3.1415) / 180 ) ) * 6380 asc limit 10';

                    where = " and is_enable =1 and departure_time>'" + current_time + "' order by CONVERT (departure_time USING gbk)COLLATE gbk_chinese_ci desc limit " + offset + "," + size;
                    owenrList = appDB.getOwenrList(where);
                    for (int i = 0; i < owenrList.size(); i++) {
                        if (owenrList.get(i).getUser_id() == user_id) {
                            owenrList.remove(i);
                            i--;
                            continue;
                        }
//                        if (owenrList.get(i).getCurrent_seats()==0){
//                            owenrList.remove(i);
//                            i--;
//                            continue;
//                        }
                        String address_board4DB = owenrList.get(i).getBoarding_point();
                        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(address_board4DB);

                        double o_longitude = Double.parseDouble("".equals(jsonObject.get("longitude").toString())?"-256.18":jsonObject.get("longitude").toString());
                        double o_latitude = Double.parseDouble("".equals(jsonObject.get("latitude").toString())?"-256.18":jsonObject.get("latitude").toString());

                        //通过经纬度获取距离
                        distance = RangeUtils.getDistance(p_latitude, p_longitude, o_latitude, o_longitude);
                        if (distance <= query_distance) {
                            nearByOwenrList.add(owenrList.get(i));
                        }
                    }
                    if (nearByOwenrList.size() != 0) {
                        count = nearByOwenrList.size();
                        result = AppJsonUtils.getNearByOwnerList(nearByOwenrList, page, size, count);
                        json = AppJsonUtils.returnSuccessJsonString(result, "附近车主列表获取成功");
                    } else {
                        result = AppJsonUtils.getNearByOwnerList(nearByOwenrList, page, size, count);
                        json = AppJsonUtils.returnFailJsonString(result, "您的附近暂时还没有车主出现哦");
                    }
                    break;
                case "nearby_passenger":
                    //获取车主经纬度
                    double o_lon = Double.parseDouble(request.getParameter("p_longitude"));
                    double o_lat = Double.parseDouble(request.getParameter("p_latitude"));
                    //符合要求的车主列表
                    List<PassengerOrder> passengerList = null;
                    //乘客附近的车主列表
                    List<PassengerOrder> nearByPassengerList = new ArrayList();
                    where = " and is_enable =1 and order_status=0 and departure_time >'" + current_time + "' order by CONVERT (departure_time USING gbk)COLLATE gbk_chinese_ci desc limit " + offset + "," + size;
                    passengerList = appDB.getPassengerList1(where);
                    for (int i = 0; i < passengerList.size(); i++) {
                        if (passengerList.get(i).getUser_id() == user_id) {
                            passengerList.remove(i);
                            i--;
                            continue;
                        }
                        String address_board4DB = passengerList.get(i).getBoarding_point();
                        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(address_board4DB);
                        //乘客位置经纬度
                        double p_lon = Double.parseDouble("".equals(jsonObject.get("longitude").toString())?"-256.18":jsonObject.get("longitude").toString());
                        double p_lat = Double.parseDouble("".equals(jsonObject.get("latitude").toString())?"-256.18":jsonObject.get("latitude").toString());

                        //通过经纬度获取距离
                        distance = RangeUtils.getDistance(o_lat, o_lon, p_lat, p_lon);
                        if (distance <= query_distance) {
                            nearByPassengerList.add(passengerList.get(i));
                        }
                    }
                    if (nearByPassengerList.size() != 0) {
                        count = nearByPassengerList.size();
                        result = AppJsonUtils.getNearByPassengerList(nearByPassengerList, page, size, count);
                        json = AppJsonUtils.returnSuccessJsonString(result, "附近乘客列表获取成功！");
                    } else {
                        result = AppJsonUtils.getNearByPassengerList(nearByPassengerList, page, size, count);
                        json = AppJsonUtils.returnFailJsonString(result, "您的附近暂时还没有乘客出现哦！");
                    }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }
}
