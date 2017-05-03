package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.Campaign;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.domain.PassengerOrder;
import com.cyparty.laihui.domain.PassengerPublishInfo;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.OssUtil;
import com.cyparty.laihui.utilities.TestUtils;
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
import java.util.ArrayList;
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
                        String image_oss = "";
                        String filePath = Utils.fileImgUpload("screenshot", request);
                        if (filePath != null && !filePath.trim().equals("")) {
                            String image_local = filePath.substring(filePath.indexOf("upload"));
                            String arr[] = image_local.split("\\\\");
                            image_oss = arr[arr.length - 1];
                            try {
                                if (ossUtil.uploadFileWithResult(request, image_oss, image_local)) {
                                    image_oss = "https://laihuipincheoss.oss-cn-qingdao.aliyuncs.com/" + image_oss;
                                }
                            } catch (Exception e) {
                                image_oss = null;
                            }
                        } else {
                            image_oss = null;
                        }
                        if (source != null && source.equals("iOS")) {
                            //iOS用户反馈
                            appDB.createSuggestion(user_id, suggestion, contact, 1, image_oss);
                        } else {
                            //android用户反馈
                            appDB.createSuggestion(user_id, suggestion, contact, 0, image_oss);
                        }
                        result.put("url", image_oss);
                        json = AppJsonUtils.returnSuccessJsonString(result, "您的建议我们已收到，感谢支持！");
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

    /**
     * 首页搜索
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public ResponseEntity<String> search(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        List<PassengerPublishInfo> finalList = new ArrayList<PassengerPublishInfo>();//最终返回的list
        List<PassengerPublishInfo> searchList = new ArrayList<PassengerPublishInfo>();//匹配的list
        List<PassengerPublishInfo> upList = new ArrayList<PassengerPublishInfo>();//上级list
        JSONObject result = new JSONObject();
        String json = "";
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        String content = request.getParameter("content");
        if (page != null && !page.equals("") && size != null && !size.equals("") && content != null && !content.equals("")) {
            Integer pageSize = Integer.parseInt(size);
            Integer pageNo = Integer.parseInt(page);
            int start = (pageNo - 1) * pageSize;
            String where = "where departure_address_code = " + content + " and destination_address_code = " + content + " and is_enable = 1 order by create_time desc";
            searchList = appDB.searchByContent(where);
            String code = content.substring(0, 4);
            String upWhere = "where departure_address_code != " + content + " and destination_address_code != " + content +
                    " and departure_address_code like '" + code + "%' and destination_address_code like '" + code + "%' and is_enable = 1 order by destination_address_code desc";
            upList = appDB.searchUp(upWhere);
            for (PassengerPublishInfo passengerOrder : searchList) {
                finalList.add(passengerOrder);
            }
            for (PassengerPublishInfo po : upList) {
                finalList.add(po);
            }
            if (start>finalList.size()){
                result.put("list", new ArrayList<PassengerPublishInfo>() {
                });
            }else {
                if ((pageNo * pageSize) > finalList.size()) {
                    result.put("list", finalList.subList(start, finalList.size()));
                } else {
                    result.put("list", finalList.subList(start, start + pageSize));
                }
            }
            json = AppJsonUtils.returnSuccessJsonString(result, "搜索成功");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
        } else {
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数有误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
        }
    }
}
