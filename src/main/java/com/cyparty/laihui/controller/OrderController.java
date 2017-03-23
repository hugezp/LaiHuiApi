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
 * Created by Administrator on 2017/3/11.
 */
@Controller
@ResponseBody
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class OrderController {
    @Autowired
    AppDB appDB;

    /**
     * 待处理订单列表模块（未使用）
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pending/order", method = RequestMethod.POST)
    public ResponseEntity<String> order(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        int user_id = 0;
        try {
            String action = request.getParameter("action");
            String token = request.getParameter("token");
            if (token != null && token.length() == 32) {
                user_id = appDB.getIDByToken(token);

            } else {
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
            switch (action) {
                case "passenger_order":
                    result = AppJsonUtils.order(appDB, user_id, 1, "passenger");
                    json = AppJsonUtils.returnSuccessJsonString(result, "乘客待处理订单列表获取成功");
                    break;
                case "owner_order":
                    result = AppJsonUtils.order(appDB, user_id, 1, "owner");
                    json = AppJsonUtils.returnSuccessJsonString(result, "司机待处理订单列表获取成功");
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
