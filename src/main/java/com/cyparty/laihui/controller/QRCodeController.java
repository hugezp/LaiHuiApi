package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.domain.Popularize;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.ConfigUtils;
import com.cyparty.laihui.utilities.OssUtil;
import com.cyparty.laihui.utilities.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 用户二维码controller
 * Created by YangGuang on 2017/4/25.
 */
@Controller
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class QRCodeController {

    @Autowired
    OssUtil ossUtil;

    @Autowired
    AppDB appDB;

    /**
     * 创建二维码
     */
    @ResponseBody
    @RequestMapping(value = "/createQRCode", method = RequestMethod.POST)
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
                Popularize popularize = appDB.getPopularById(where);
                if (popularize != null) {
                    //专业推广
                    content = ConfigUtils.PROFESSIONAL_PROMOTION + popularize.getPopularize_code();
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
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String pathName = "F://qrcode//" + uuid + ".png";
        try {
            QRCodeUtil.create_image(content, pathName);
        } catch (Exception e) {
            json = AppJsonUtils.returnFailJsonString(result, "二维码创建失败！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
        }
        result.put("pathName", pathName);
        json = AppJsonUtils.returnSuccessJsonString(result, "创建成功");
        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
    }

}
