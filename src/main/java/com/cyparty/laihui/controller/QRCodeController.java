package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.ConfigUtils;
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
 * 扫码跳转的链接controller
 * Created by YangGuang on 2017/4/25.
 */
@Controller
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class QRCodeController {


    @Autowired
    private AppDB appDB;

    /**
     * 二维码跳转的链接1
     */
    @ResponseBody
    @RequestMapping(value = "/qrcodeLink", method = RequestMethod.POST)
    public ResponseEntity<String> createQRCode(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        String content = "";
        String token = request.getParameter("token");
        //根据token查出用户id
        if (token != null && !token.equals("")) {
            int userId = appDB.getIDByToken(token);
            if (userId != 0) {
                //根据id判断推广人类型
                String where = "where popularize_id = " + userId + " and is_enable = 1 and level = 0";
                String code = appDB.getPopularById(where);
                if (code != null && !code.equals("")) {
                    //专业推广
                    content = ConfigUtils.PROFESSIONAL_PROMOTION + code;
                } else {
                    //全民代理
                    content = ConfigUtils.NATIONAL_AGENT + token;
                }
            } else {
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "无效token");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }
        } else {
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
        }
        result.put("link", content);
        json = AppJsonUtils.returnSuccessJsonString(result, "查询链接成功");
        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
    }

}
