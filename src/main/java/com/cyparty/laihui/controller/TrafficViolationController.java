package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.CarOwnerInfo;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.HttpUtils;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dupei on 2017/3/17 0017.
 */

/**
 * 违章查询模块
 */
@Controller
@ResponseBody
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class TrafficViolationController {
    @Autowired
    AppDB appDB;

    @ResponseBody
    @RequestMapping(value = "/illegal/inquire", method = RequestMethod.POST)
    public ResponseEntity<String> getCommonRoute(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            //车牌号
            String car_plate = request.getParameter("car_plate");
            //车架号
            String frameno = request.getParameter("frameno");
            String lsnum = car_plate.substring(1, 7);
            String lsprefix = car_plate.substring(0, 1);

            String host = "http://jisuqgclwz.market.alicloudapi.com";
            String path = "/illegal/query";
            String method = "GET";
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "APPCODE 3bd0bd94a2f24acaa375dc1e7f44ea9f");

            Map<String, String> querys = new HashMap<String, String>();
            querys.put("engineno", "123456");
            querys.put("frameno", frameno);
            querys.put("lsnum", lsnum);
            querys.put("lsprefix", lsprefix);
            try {
                HttpResponse response;

                response = HttpUtils.doGet(host, path, method, headers, querys);
                result.put("data", EntityUtils.toString(response.getEntity()));
                json = AppJsonUtils.returnFailJsonString(result, "查询成功！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                json = AppJsonUtils.returnFailJsonString(result, "您输入的信息有误，请核对后再输！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }


    }

}




