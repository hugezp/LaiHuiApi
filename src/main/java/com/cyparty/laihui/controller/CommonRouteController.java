package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.DriverAndCar;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.domain.PassengerOrder;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.ConfigUtils;
import com.cyparty.laihui.utilities.RangeUtils;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/3/8.
 */
@Controller
@ResponseBody
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class CommonRouteController {
    @Autowired
    AppDB appDB;

    /**
     * 常用路线模块（添加常用路线、删除、更新、查询）
     *
     * @param request
     * @return
     */
    @Transactional(readOnly=false)
    @ResponseBody
    @RequestMapping(value = "/common/route", method = RequestMethod.POST)
    public ResponseEntity<String> getCommonRoute(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        int id = 0;
        String json = "";
        String where = "";
        boolean is_success = false;
        String action = request.getParameter("action");
        String token = request.getParameter("token");
        int user_id = 0;
        if (token != null && token.length() == 32) {
            user_id = appDB.getIDByToken(token);
        } else {
            result.put("error_code", ErrorCode.TOKEN_EXPIRED);
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
        int is_enable = 1;
        switch (action) {
            //添加常用路线
            case "add":
                if (user_id > 0) {
                    int is_default = 0;
                    where = " where user_id=" + user_id + " and is_enable=1";
                    if (appDB.getCommonRoute(where).size()>2){
                        json = AppJsonUtils.returnFailJsonString(result, "您的常用路线数量达到上限！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    if (appDB.getCommonRoute(where).size()==0){
                        is_default = 1;
                    }
                    String departure_city = request.getParameter("departure_city");
                    String departure_address = request.getParameter("departure_address");
                    String departure_lon = request.getParameter("departure_lon");
                    String departure_lat = request.getParameter("departure_lat");
                    String destinat_city = request.getParameter("destinat_city");
                    String destinat_address = request.getParameter("destinat_address");
                    String destinat_lon = request.getParameter("destinat_lon");
                    String destinat_lat = request.getParameter("destinat_lat");

                    is_success = appDB.createCommonRoute(user_id, departure_city, departure_address, departure_lon, departure_lat, destinat_city, destinat_address, destinat_lon, destinat_lat, is_enable,is_default);
                    if (is_success) {
                        json = AppJsonUtils.returnSuccessJsonString(result, "常用路线添加成功");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.PARAMETER_WRONG);
                        json = AppJsonUtils.returnFailJsonString(result, "常用路线添加失败！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }

                }
                result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            //逻辑上的删除，将其设为不可用
            case "delete":
                boolean is_success2 = true;
                //常用路线记录id
                id = Integer.parseInt(request.getParameter("id"));
                where = " where id="+id+" and is_default=1";
                int count = appDB.getCommonRoute(where).size();
                if (count>0){
                    where = " where user_id=" + user_id + " and is_enable=1";
                    if (appDB.getCommonRoute(where).size()>1){
                        where = " set is_default=1 where user_id="+user_id+" and is_enable=1 and is_default=0 limit 1";
                        is_success2 = appDB.update("pc_common_route", where);
                    }
                    where = " set is_enable=0 where id=" + id;
                    is_success = appDB.update("pc_common_route", where);
                     if (is_success&&is_success2) {
                        json = AppJsonUtils.returnSuccessJsonString(result, "删除成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.PARAMETER_WRONG);
                        json = AppJsonUtils.returnFailJsonString(result, "抱歉，此删除无效，请重新尝试！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                }
                    where = " set is_enable=0 where id=" + id;
                    is_success = appDB.update("pc_common_route", where);
                    if (is_success) {
                        json = AppJsonUtils.returnSuccessJsonString(result, "删除成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.PARAMETER_WRONG);
                        json = AppJsonUtils.returnFailJsonString(result, "抱歉，此删除无效，请重新尝试！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
            //修改
            case "update":
                //路线id
                id = Integer.parseInt(request.getParameter("id"));
                //出发城市
                String departure_city = request.getParameter("departure_city");
                //出发地点
                String departure_address = request.getParameter("departure_address");
                //目的城市
                String destinat_city = request.getParameter("destinat_city");
                //目的地点
                String destinat_address = request.getParameter("destinat_address");
                String departure_lon = request.getParameter("departure_lon");
                String departure_lat = request.getParameter("departure_lat");
                String destinat_lon = request.getParameter("destinat_lon");
                String destinat_lat = request.getParameter("destinat_lat");

                where = " set departure_city = '" + departure_city + "',departure_address = '" + departure_address + "',destinat_city = '" + destinat_city + "',destinat_address = '" + destinat_address + "',destinat_lat = '" + destinat_lat + "',destinat_lon = '" + destinat_lon + "',departure_lat = '" + departure_lat + "',departure_lon = '" + departure_lon + "'where id=" + id;
               is_success = appDB.update("pc_common_route", where);
                if (is_success) {
                    json = AppJsonUtils.returnSuccessJsonString(result, "您的路线更新成功！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                } else {
                    result.put("error_code", ErrorCode.PARAMETER_WRONG);
                    json = AppJsonUtils.returnFailJsonString(result, "抱歉！您的路线更新无效，请重新尝试");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
             //查询
            case "show":
                where = " where user_id=" + user_id + " and is_enable=1 order by is_default desc";
                result = AppJsonUtils.commonRoute(appDB, user_id, where);
                json = AppJsonUtils.returnSuccessJsonString(result, "路线列表获取成功！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                //常用路线开关
            case "switch":

                    //常用路线记录id
                    id = Integer.parseInt(request.getParameter("id"));
                    String status = request.getParameter("status");
                    if (status.equals("0")){
                        where = " set is_switch=0 where id=" + id;
                       is_success = appDB.update("pc_common_route", where);
                        if (is_success) {
                            json = AppJsonUtils.returnSuccessJsonString(result, "关闭成功！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            result.put("error_code", ErrorCode.PARAMETER_WRONG);
                            json = AppJsonUtils.returnFailJsonString(result, "由于系统原因关闭失败，请见谅！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                    }else {
                        where = " set is_switch=1 where id=" + id;
                        boolean is_success1 = appDB.update("pc_common_route", where);
                        if (is_success1) {
                            json = AppJsonUtils.returnSuccessJsonString(result, "开启成功！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            result.put("error_code", ErrorCode.PARAMETER_WRONG);
                            json = AppJsonUtils.returnFailJsonString(result, "由于系统原因开启失败，请见谅！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                    }
                //默认常用路线设置
            case "default":
                //将全部常用路线设置为非默认
                    where = " set is_default=0 where user_id=" + user_id + " and is_enable=1";
                    appDB.update("pc_common_route", where);
                    //常用路线记录id
                    id = Integer.parseInt(request.getParameter("id"));
                    where = " set is_default=1 where id=" + id;
                    is_success = appDB.update("pc_common_route", where);
                    if (is_success) {
                        json = AppJsonUtils.returnSuccessJsonString(result, "默认路线成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.PARAMETER_WRONG);
                        json = AppJsonUtils.returnFailJsonString(result, "由于系统原因设置失败，请见谅！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
        }
        result.put("error_code", ErrorCode.PARAMETER_WRONG);
        json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);

    }

    @ResponseBody
    @RequestMapping(value = "/common/route/search", method = RequestMethod.POST)
    public ResponseEntity<String> search(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        String where = "";
        int user_id = 0;
        int count = 0;
        //我的行程距离
        double my_distance = 0.0;
        //起点距离
        double start_point_distance = 0.0;
        //终点距离
        double end_point_distance = 0.0;
        //查询范围
        double query_distance = ConfigUtils.QUERY_DISTANCE;
        String current_time = Utils.getCurrentTimeSubOrAddHour(0);
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
                result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
            int offset = page * size;
            double departure_lon = Double.parseDouble(request.getParameter("departure_lon"));
            double departure_lat = Double.parseDouble(request.getParameter("departure_lat"));
            double destinat_lon = Double.parseDouble(request.getParameter("destinat_lon"));
            double destinat_lat = Double.parseDouble(request.getParameter("destinat_lat"));
            switch (action) {
                //获取经纬度
                case "nearby_owner":
                    //符合要求的车主列表
                    where = " where is_enable =1 and p.user_id != " + user_id + " and departure_time > '" + current_time + "' and init_seats != 0 having s_distance <= " + query_distance + " and e_distance <= " + query_distance + " order by CONVERT (departure_time USING gbk)COLLATE gbk_chinese_ci asc limit " + offset + " ,"+size;
                    List<DriverAndCar> owenrList = appDB.getOwenrList1(where,departure_lon,departure_lat,destinat_lon,destinat_lat);
                    if (owenrList.size()>0) {
                        //通过经纬度获取距离
                        for (DriverAndCar owenr:owenrList){
                            my_distance = RangeUtils.getDistance(departure_lat, departure_lon, destinat_lat, destinat_lon);
                            start_point_distance = owenr.getS_distance();
                            end_point_distance = owenr.getE_distance();
                            String suitability = RangeUtils.getSuitability(my_distance, start_point_distance, end_point_distance);
                            owenr.setSuitability(suitability);
                            owenr.setStart_point_distance(start_point_distance);
                            owenr.setEnd_point_distance(end_point_distance);
                        }
                        count = owenrList.size();
                        result = AppJsonUtils.getNearByOwnerList(owenrList, page, size, count, appDB);
                        json = AppJsonUtils.returnSuccessJsonString(result, "附近车主列表获取成功");
                    } else {
                        result = AppJsonUtils.getNearByOwnerList(owenrList, page, size, count, appDB);
                        json = AppJsonUtils.returnFailJsonString(result, "您的附近暂时还没有车主出现哦");
                    }
                    break;
                case "nearby_passenger":
                    //符合要求的乘客列表
                    where = " where is_enable =1 and a.user_id != " + user_id + " and departure_time >'" + current_time + "' having s_distance <= " + query_distance + " and e_distance <= " + query_distance + " order by convert (departure_time USING gbk)COLLATE gbk_chinese_ci asc limit " + offset + " ,"+size;
                    List<PassengerOrder> passengerList = appDB.getPassengerList(where,departure_lon,departure_lat,destinat_lon,destinat_lat);
                    if (passengerList.size()>0){
                        //通过经纬度获取距离
                        for (PassengerOrder passenger:passengerList){
                            my_distance = RangeUtils.getDistance(departure_lat, departure_lon, destinat_lat, destinat_lon);
                            start_point_distance = passenger.getS_distance();
                            end_point_distance = passenger.getE_distance();
                            String suitability = RangeUtils.getSuitability(my_distance, start_point_distance, end_point_distance);
                            passenger.setSuitability(suitability);
                            passenger.setStart_point_distance(start_point_distance);
                            passenger.setEnd_point_distance(end_point_distance);
                        }

                        count = passengerList.size();
                        result = AppJsonUtils.getNearByPassengerList(passengerList, page, size, count);
                        json = AppJsonUtils.returnSuccessJsonString(result, "附近乘客列表获取成功！");
                    }else {
                        result = AppJsonUtils.getNearByPassengerList(passengerList, page, size, count);
                        json = AppJsonUtils.returnFailJsonString(result, "您的附近暂时还没有乘客出现哦！");
                    }
                    break;
                default:
                    result.put("error_code", ErrorCode.PARAMETER_WRONG);
                    json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }
}