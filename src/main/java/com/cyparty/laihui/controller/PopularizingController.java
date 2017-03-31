package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.Popularizing;
import com.cyparty.laihui.domain.Popularize;
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
@RequestMapping( value ="/api/app",method = RequestMethod.POST)
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
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json="";
//        String URL = request.getRequestURL().toString();
//        String popularize_code = request.getQueryString();
        String popularize_code = request.getParameter("code");
        String popularizing_mobile = request.getParameter("mobile");
        boolean is_true = false;
        if(null != popularize_code && null != popularizing_mobile){
            List<Popularizing> popularizes = appDB.getPopularize(popularizing_mobile);
            List<User> users = appDB.getUserList(" where user_mobile ="+popularizing_mobile);
            //将系统存在的手机号，和已经推广的手机号不再计入推广人的业绩
            if(popularizes.size() == 0 && users.size() == 0){
                is_true = appDB.createPopularizing(popularize_code,popularizing_mobile);
                if(is_true){
                    json = AppJsonUtils.returnSuccessJsonString(result, "推广添加成功！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }else{
                    json = AppJsonUtils.returnFailJsonString(result, "推广添加失败！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
            }else{
                json = AppJsonUtils.returnFailJsonString(result, "手机号已存在不能重复添加！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
        }else{
            json = AppJsonUtils.returnFailJsonString(result, "请求参数错误！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    /***
     * 分享推广人推广码
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/share", method = RequestMethod.POST)
    public ResponseEntity<String> share(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        String token = request.getParameter("token");
        if (token != null && token.length() == 32) {
            int user_id = appDB.getIDByToken(token);
            String now_where = " where _id = "+user_id +" and is_validated =1";
            List<User> userList = appDB.getUserList(now_where);
            //判断该用户是不是实名认证的用户
            if (userList.size() > 0) {
                List<Popularize> popularizeList = appDB.getPopular(user_id);
                if(popularizeList.size()>0){
                    Popularize popularize = popularizeList.get(0);
                    String code = popularize.getPopularize_code();
                    int level =popularize.getLevel();
                    if(level <= 5){
                        result.put("code",code);
                        json = AppJsonUtils.returnSuccessJsonString(result, "推广码获取成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }else{
                        json = AppJsonUtils.returnFailJsonString(result, "推广码获取失败！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                }else{
                    json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }else{
                json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
            }
        }else{
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
    }
}
