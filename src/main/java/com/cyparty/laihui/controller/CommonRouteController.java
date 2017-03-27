package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.utilities.AppJsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/common/route", method = RequestMethod.POST)
    public ResponseEntity<String> getCommonRoute(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        int id = 0;
        String json = "";
        String where = "";
        String action = request.getParameter("action");
        String token = request.getParameter("token");
        int user_id = 0;
        int is_enable = 1;
        switch (action) {
            case "add":
                if (token != null && token.length() == 32) {
                    user_id = appDB.getIDByToken(token);

                } else {
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
                if (user_id > 0) {
                    String departure_city = request.getParameter("departure_city");
                    String departure_address = request.getParameter("departure_address");
                    String departure_lon = request.getParameter("departure_lon");
                    String departure_lat = request.getParameter("departure_lat");
                    String destinat_city = request.getParameter("destinat_city");
                    String destinat_address = request.getParameter("destinat_address");
                    String destinat_lon = request.getParameter("destinat_lon");
                    String destinat_lat = request.getParameter("destinat_lat");

                    boolean is_success = appDB.createCommonRoute(user_id, departure_city, departure_address, departure_lon, departure_lat, destinat_city, destinat_address, destinat_lon, destinat_lat, is_enable);
                    if (is_success) {
                        json = AppJsonUtils.returnSuccessJsonString(result,"常用路线添加成功");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.getParameter_wrong());
                        json = AppJsonUtils.returnFailJsonString(result, "常用路线添加失败！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }

                }
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                //逻辑上的删除，将其设为不可用
            case "delete":
                if (token != null && token.length() == 32) {
                    user_id = appDB.getIDByToken(token);

                } else {
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
                if (user_id > 0) {
                    //常用路线记录id
                    id = Integer.parseInt(request.getParameter("id"));
                    where = " set is_enable=0 where id=" + id;
                    boolean is_success = appDB.update("pc_common_route", where);
                    if (is_success) {
                        json = AppJsonUtils.returnSuccessJsonString(result, "删除成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.getParameter_wrong());
                        json = AppJsonUtils.returnFailJsonString(result, "抱歉，此删除无效，请重新尝试！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                }
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
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
//                String destinat_city = "2";
//                String destinat_address = "2";
//                String departure_city = "2";
//                String departure_address = "2";
                where = " set departure_city = '" + departure_city + "',departure_address = '" + departure_address + "',destinat_city = '" + destinat_city + "',destinat_address = '" + destinat_address + "'where id=" + id;
                boolean is_success = appDB.update("pc_common_route", where);
                if (is_success) {
                    json = AppJsonUtils.returnSuccessJsonString(result, "您的路线更新成功！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                } else {
                    result.put("error_code", ErrorCode.getParameter_wrong());
                    json = AppJsonUtils.returnFailJsonString(result, "抱歉！您的路线更新无效，请重新尝试");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
            case "show":
                if (token != null && token.length() == 32) {
                    user_id = appDB.getIDByToken(token);

                } else {
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
                //id = Integer.parseInt(request.getParameter("id"));
                where = " where user_id=" + user_id + " and is_enable=1";
                result = AppJsonUtils.commonRoute(appDB,user_id, where);
                json = AppJsonUtils.returnSuccessJsonString(result, "路线列表获取成功！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);

        }

        result.put("error_code", ErrorCode.getParameter_wrong());
        json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);

    }
}
