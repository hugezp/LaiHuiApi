package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.IDSNValidated;
import com.cyparty.laihui.utilities.Utils;
import com.cyparty.laihui.utilities.ValidateUtils;
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

import static com.sun.jmx.snmp.SnmpStatusException.readOnly;

/**
 * Created by Administrator on 2017/4/26.
 */
@Controller
public class Test {
    @Autowired
    AppDB appDB;
    @ResponseBody
    @RequestMapping(value = "/test",method = RequestMethod.POST)
    public ResponseEntity<String> aa(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json ="";
        String name = request.getParameter("name");
        String idsn = request.getParameter("idsn");
        String user = ValidateUtils.getUrl(idsn,name);
        String body = Utils.getJsonObject(user, "showapi_res_body");
        String userCode = Utils.getJsonObject(body, "code");
        if ("0".equals(userCode)) {
            boolean is_success = IDSNValidated.getValidateCode(idsn);
            if (!is_success) {
                json = AppJsonUtils.returnFailJsonString(result, "身份证号码有误，请重新核对！");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }else {
                json = AppJsonUtils.returnFailJsonString(result, "成功！");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }
        }
        return null;
    }
}
