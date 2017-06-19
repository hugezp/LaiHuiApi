package com.cyparty.laihui.controller.willArrive;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.service.ArriveService;
import com.cyparty.laihui.utilities.AppJsonUtils;
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

/**
 * 查询必达单
 * Created by pangzhenpeng on 2017/6/16.
 */
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
@Controller
public class ArriveController {
    @Autowired
    AppDB appDB;

    @ResponseBody
    @RequestMapping(value = "/search/arrive")
    public ResponseEntity<String> search(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            //判断用户标识
            String mobile = request.getParameter("mobile");
            if (request.getParameter("token") != null && request.getParameter("token").length() == 32) {
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
                try {
                    //获取json数据
                    result = ArriveService.getArriveListJson(mobile, appDB, page, size);
                } catch (Exception e) {
                    result.put("error_code", ErrorCode.getError_system());
                    json = AppJsonUtils.returnFailJsonString(result, "服务器错误!");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }


            } else {
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }

        } catch (Exception e) {
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
        if (Integer.parseInt(result.get("count").toString()) == 0) {
            json = AppJsonUtils.returnFailJsonString(result, "暂无数据！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
        json = AppJsonUtils.returnSuccessJsonString(result, "数据获取成功!");
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }
    /**
     * 查看必达单详情
     */
    @ResponseBody
    @RequestMapping(value = "/passenger/info")
    public ResponseEntity<String> info(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            int carId = Integer.parseInt(request.getParameter("car_id"));
            if (request.getParameter("token") != null && request.getParameter("token").length() == 32) {
                //获取乘客车单ID

                result = AppJsonUtils.getPassengerDepartureList(appDB, 0, 0, 0, 0, carId,0.0,0.0,0.0,0.0);
                String data = Utils.getJsonObject(result.toJSONString(), "data");
                if ("[]".equals(data) || null == data) {
                    json = AppJsonUtils.returnFailJsonString(result, "乘客出行信息详情获取失败！");
                    return new ResponseEntity<>(json, httpHeaders, HttpStatus.OK);
                } else {
                    json = AppJsonUtils.returnSuccessJsonString(result, "乘客出行信息详情获取成功！");
                    return new ResponseEntity<>(json, httpHeaders, HttpStatus.OK);
                }
            } else {
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token!");
                return new ResponseEntity<>(json, httpHeaders, HttpStatus.OK);
            }
        } catch (Exception e) {
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误!");
            return new ResponseEntity<>(json, httpHeaders, HttpStatus.OK);
        }
    }
}
