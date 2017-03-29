package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.Popularizing;
import com.cyparty.laihui.domain.User;
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
 * Created by dupei on 2017/3/27 0027.
 */
//存储推广人推广的手机号码
    @Controller
    @ResponseBody
    @RequestMapping( value ="/app/api",method = RequestMethod.POST)
public class PopularizingController {
    @Autowired
    AppDB appDB;
    /***
     * 记录推广人推广手机号
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/popularize", method = RequestMethod.POST)
    public ResponseEntity<String> popularize(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json="";
//        String URL = request.getRequestURL().toString();
        String popularize_code = request.getQueryString();
        String popularizing_mobile = request.getParameter("mobile");
        boolean is_true = false;
        if(null != popularize_code && null != popularizing_mobile){
            List<Popularizing> popularizes = appDB.getPopularize(popularizing_mobile);
            List<User> users = appDB.getUserList(" where user_mobile ="+popularizing_mobile);
            //将系统存在的手机号，和已经推广的手机号不再计入推广人的业绩
            if(popularizes.size() == 0 && users.size() == 0){
                is_true = appDB.createPopularizing(popularize_code,popularizing_mobile);
            }else{
                json = AppJsonUtils.returnSuccessJsonString(result, "手机号已存在不能重复添加！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            if(is_true){
                json = AppJsonUtils.returnSuccessJsonString(result, "推广添加成功！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }else{
                json = AppJsonUtils.returnSuccessJsonString(result, "推广添加失败！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            json = AppJsonUtils.returnSuccessJsonString(result, "请求参数错误！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

}
