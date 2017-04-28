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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static com.sun.jmx.snmp.SnmpStatusException.readOnly;

/**
 * Created by Administrator on 2017/4/26.
 */
@Controller
public class Test {
    @Autowired
    AppDB appDB;
    @Transactional(readOnly=false)
    @RequestMapping(value = "/test",method = RequestMethod.POST)
    public ResponseEntity<String> aa(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json ="";
       int user_id = Integer.parseInt( request.getParameter("user_id"));
        String where = " set is_default=0 where user_id=" + user_id + " and is_enable=1";
        appDB.update("pc_common_route", where);
        //常用路线记录id
       int id = Integer.parseInt(request.getParameter("id"));
        where = " set is_default=1111 where id=" + id;
       boolean is_success = appDB.update("pc_common_route", where);
        if (is_success) {
            json = AppJsonUtils.returnSuccessJsonString(result, "默认路线成功！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        } else {
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "由于系统原因设置失败，请见谅！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
    }
}
