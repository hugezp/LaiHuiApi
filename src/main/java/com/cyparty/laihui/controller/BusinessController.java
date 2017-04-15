package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.Business;
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
import java.util.List;

/**
 * Created by Administrator on 2017/4/13.
 */
@Controller
@ResponseBody
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class BusinessController {
    @Autowired
    AppDB appDB;
    @RequestMapping(value = "/business", method = RequestMethod.POST)
    public ResponseEntity<String> business(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        String json = "";
        JSONObject result = new JSONObject();
        try {
            String business_name = request.getParameter("business_name");
            String business_mobile = request.getParameter("business_mobile");
            String address = request.getParameter("address");
            String cooperation_way = request.getParameter("cooperation_way");
            String cooperation_description = request.getParameter("cooperation_description");
            String where = " where business_name='"+business_name+"' and business_mobile='"+business_mobile+"'";
            List<Business>  businessList = appDB.getMerchantJion(where);
            if (businessList.size()>0){
                json = AppJsonUtils.returnFailJsonString(result,"该信息已经被提交过了，无法再次提交！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
            boolean is_success = appDB.createMerchantJion(business_name,business_mobile,address,cooperation_way,cooperation_description);
            if (is_success){
                result.put("business_name",business_name);
                result.put("business_mobile",business_mobile);
                result.put("address",address);
                result.put("cooperation_way",cooperation_way);
                result.put("cooperation_description",cooperation_description);
                json = AppJsonUtils.returnSuccessJsonString(result,"感谢您的加入，稍后会有工作人员与您联系!！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }else {
                json = AppJsonUtils.returnFailJsonString(result,"加盟信息提交失败，请检查信息是否正确！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
        }catch (Exception e){
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result,"获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }

    }
}
