package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.Campaign;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.OssUtil;
import com.cyparty.laihui.utilities.TestUtils;
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
 * Created by zhu on 2016/5/11.
 */
@Controller
@ResponseBody
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class APIController {
    @Autowired
    AppDB appDB;
    @Autowired
    OssUtil ossUtil;
    @Autowired
    TestUtils testUtils;

    /**
     * 用户反馈模块
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/suggestion", method = RequestMethod.POST)
    public ResponseEntity<String> suggestion(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            String action = request.getParameter("action");
            String token = request.getParameter("token");
            int user_id = 0;
            if (token != null) {
                try {
                    user_id = appDB.getIDByToken(token);
                } catch (Exception e) {
                    user_id = 0;
                    e.printStackTrace();
                }
            }
            switch (action) {
                case "add":
                    if (user_id > 0) {
                        String suggestion = request.getParameter("advice");
                        String contact = request.getParameter("email");
                        String source = request.getParameter("source");

                        if (source != null && source.equals("iOS")) {
                            //iOS用户反馈
                            appDB.createSuggestion(user_id, suggestion, contact, 1);
                        } else {
                            //android用户反馈
                            appDB.createSuggestion(user_id, suggestion, contact, 0);
                        }
                        json = AppJsonUtils.returnSuccessJsonString(result, "意见反馈成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.getToken_expired());
                        json = AppJsonUtils.returnFailJsonString(result, "非法token!");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
            }
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/apk/update", method = RequestMethod.GET)
    public ResponseEntity<String> apk_update(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        String json = "";
        //testUtils.testAsyncMethod();
        String source = request.getParameter("source");
        if (source != null && source.equals("iOS")) {
            json = AppJsonUtils.getApkUpdated(appDB, 1).toJSONString();
        } else {
            json = AppJsonUtils.getApkUpdated(appDB, 0).toJSONString();
        }
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }

    /***
     * 轮播图模块
     */
    @ResponseBody
    @RequestMapping(value = "/carousel", method = RequestMethod.POST)
    public ResponseEntity<String> carousel(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            String action = request.getParameter("action");
            boolean is_success = false;
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
            int id = 0;
            switch (action) {
                case "show":
                    if (request.getParameter("id") != null && !request.getParameter("id").isEmpty()) {
                        id = Integer.parseInt(request.getParameter("id"));
                    } else {
                        id = 0;
                    }
                    json = AppJsonUtils.returnSuccessJsonString(AppJsonUtils.getCarouselJson(appDB, page, size, id), "轮播图信息获取成功");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
    }

    /***
     * 弹出广告模块
     */
    @ResponseBody
    @RequestMapping(value = "/pop_up_ad", method = RequestMethod.POST)
    public ResponseEntity<String> pop_up_ad(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            String action = request.getParameter("action");

            switch (action) {
                case "show":
                    result = AppJsonUtils.getPopUpAdJson(appDB, 0);
                    if (result.isEmpty()) {
                        json = AppJsonUtils.returnFailJsonString(result, "沒有广告");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        json = AppJsonUtils.returnSuccessJsonString(result, "弹出广告获取成功");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }

                case "show_list":
                    result = AppJsonUtils.getPopUpAdJson(appDB, 1);
                    if (result.isEmpty()) {
                        json = AppJsonUtils.returnFailJsonString(result, "沒有广告");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        json = AppJsonUtils.returnSuccessJsonString(result, "弹出广告获取成功");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
            }
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/navigation_page", method = RequestMethod.POST)
    public ResponseEntity<String> navigation_page(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            String action = request.getParameter("action");
            switch (action) {
                case "show":
                    json = AppJsonUtils.returnSuccessJsonString(AppJsonUtils.getNavigationJson(appDB), "导航页获取成功");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/campaign")
    public ResponseEntity<String> campaign(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject jsonObject = new JSONObject();
        String token = request.getParameter("token");
        int id = 0;
        if (token != null && !token.isEmpty()) {
            try {
                id = appDB.getIDByToken(token);
            } catch (Exception e) {
                jsonObject.put("status", false);
                jsonObject.put("error_code", ErrorCode.getToken_expired());
                jsonObject.put("msg", "非法token！");
            }
        } else {
            jsonObject.put("status", false);
            jsonObject.put("error_code", ErrorCode.getToken_expired());
            jsonObject.put("msg", "非法token！");
        }
        String where = " where user_id=" + id + " and is_reg=1";
        int total = appDB.getCount("pc_campaign", where);
        jsonObject.put("status", true);
        jsonObject.put("total", total);
        jsonObject.put("pic", "http://laihui.cyparty.com/resource/h5_images/pch_campaign.jpg");
        jsonObject.put("msg", "推广人数获取成功！");

        return new ResponseEntity<>(jsonObject.toString(), responseHeaders, HttpStatus.OK);
    }
}
