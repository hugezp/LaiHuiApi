package com.cyparty.laihui.controller.willArrive;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.service.ArriveService;
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
        int userId = 0;
        try {
            //判断用户标识
            String mobile = request.getParameter("mobile");
            if (request.getParameter("token") != null && request.getParameter("token").length() == 32) {
                //String token = request.getParameter("token");
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
            result.put("error_code", ErrorCode.getToken_expired());
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
        if (Integer.parseInt(result.get("count").toString()) == 0) {
            json = AppJsonUtils.returnFailJsonString(result, "暂无数据！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
        json = AppJsonUtils.returnSuccessJsonString(result, "数据获取成功!");
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }
}
