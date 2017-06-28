package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.DriverPublishInfo;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.domain.News;
import com.cyparty.laihui.domain.UserTravelCardInfo;
import com.cyparty.laihui.utilities.*;
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
                        result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                        json = AppJsonUtils.returnFailJsonString(result, "非法token!");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
            }
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
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
     * 发现模块
     */
    @ResponseBody
    @RequestMapping(value = "/carousel", method = RequestMethod.POST)
    public ResponseEntity<String> carousel(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
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
                    result = AppJsonUtils.getCarouselJson(appDB, page, size, id);
                    result.put("partner", AppJsonUtils.getPartner(appDB));
                    String where = "";
                    result.put("news", appDB.getNewsList1(where));
                    where = " WHERE type = 12 AND is_enable = 1 AND isDel = 1 order by n.create_time desc";
                    result.put("headlines", appDB.getNewsList(where));
                    json = AppJsonUtils.returnSuccessJsonString(result, "信息获取成功");
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


    //根据新闻ID查找新闻
    @ResponseBody
    @RequestMapping(value = "/news", method = RequestMethod.POST)
    public ResponseEntity<String> news(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            int newsId = Integer.parseInt(request.getParameter("newsId"));
            String where = " WHERE  _id = " + newsId;
            List<News> newsList = appDB.getNewsList(where);
            result.put("news",newsList.get(0));
            json = AppJsonUtils.returnSuccessJsonString(result, "数据获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
        }
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }

    /**
     * 获取新闻列表
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/news/list", method = RequestMethod.POST)
    public ResponseEntity<String> newsList(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            int type_id = Integer.parseInt(request.getParameter("type_id"));
            String where = " WHERE type_id = " + type_id + " AND is_enable = 1 AND type != 12 AND isDel=1";
            List<News> newsList = appDB.getNewsList(where);
            result.put("newsList",newsList);
            json = AppJsonUtils.returnSuccessJsonString(result, "数据获取成功");
        } catch (Exception e) {
            e.printStackTrace();
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
        }
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
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
                jsonObject.put("error_code", ErrorCode.TOKEN_EXPIRED);
                jsonObject.put("msg", "非法token！");
            }
        } else {
            jsonObject.put("status", false);
            jsonObject.put("error_code", ErrorCode.TOKEN_EXPIRED);
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
     * 首页搜索,乘客搜车主
     */
    @ResponseBody
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public ResponseEntity<String> search(HttpServletRequest request) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        List<DriverPublishInfo> finalList = new ArrayList<DriverPublishInfo>();//最终返回的list
        List<DriverPublishInfo> searchList = new ArrayList<DriverPublishInfo>();//匹配的list
        List<DriverPublishInfo> upList = new ArrayList<DriverPublishInfo>();//上级list
        List<DriverPublishInfo> tempList = new ArrayList<DriverPublishInfo>();//临时list
        List<DriverPublishInfo> tempList1 = new ArrayList<DriverPublishInfo>();//临时list
        String json = "";
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        String content = request.getParameter("content");
        String word = request.getParameter("word");
        if (page != null && !page.equals("") && size != null && !size.equals("") && content != null && !content.equals("") && word != null && !word.equals("")) {
            Integer pageSize = Integer.parseInt(size);
            Integer pageNo = Integer.parseInt(page);
            int start = (pageNo - 1) * pageSize;
            int end = pageSize;
            String code = content.substring(0, 4);
            String where = "where p.is_enable = 1 and p.departure_time > '" + Utils.getCurrentTimeSubOrAddHour(-3) + "' and (departure_code = " + code +
                    " or destination_code = " + code + ") order by p.create_time desc limit " + start + "," + end;
            searchList = appDB.searchByContent(where);
            for (DriverPublishInfo passengerPublishInfo : searchList) {
                if (passengerPublishInfo.getBreakout_point().contains(word)) {
                    tempList.add(passengerPublishInfo);
                    continue;
                }
                if (passengerPublishInfo.getBoarding_point().contains(word)) {
                    tempList.add(passengerPublishInfo);
                    continue;
                }
                if (passengerPublishInfo.getDestination_address_code() == Integer.parseInt(content)) {
                    tempList1.add(passengerPublishInfo);
                    continue;
                }
                if (passengerPublishInfo.getDeparture_address_code() == Integer.parseInt(content)) {
                    tempList1.add(passengerPublishInfo);
                    continue;
                }
                upList.add(passengerPublishInfo);
            }
            if (tempList.size() > 0) {
                for (DriverPublishInfo ppi : tempList) {
                    finalList.add(ppi);
                }
            }
            if (tempList1.size() > 0) {
                for (DriverPublishInfo po : tempList1) {
                    finalList.add(po);
                }
            }
            if (upList.size() > 0) {
                for (DriverPublishInfo bean : upList) {
                    finalList.add(bean);
                }
            }
            if (finalList.size() > 0) {
                for (int i = 0; i < finalList.size(); i++) {
                    if (finalList.get(i).getCurrent_seats() == 0) {
                        finalList.remove(i);
                        i--;
                        continue;
                    }
                }
                for (DriverPublishInfo dpiBean : finalList) {
                    JSONObject result = new JSONObject();
                    result.put("mobile", dpiBean.getMobile());
                    result.put("car_id", dpiBean.getR_id());
                    result.put("price", dpiBean.getPrice());
                    result.put("departure_time", DateUtils.getProcessdTime(dpiBean.getStart_time()));
                    result.put("create_time", DateUtils.getProcessdTime(dpiBean.getCreate_time()));
                    result.put("i_province", net.sf.json.JSONObject.fromObject(dpiBean.getBoarding_point()).get("province"));
                    result.put("i_city", net.sf.json.JSONObject.fromObject(dpiBean.getBoarding_point()).get("city"));
                    String id = net.sf.json.JSONObject.fromObject(dpiBean.getBoarding_point()).get("id").toString();
                    if (id == null) {
                        result.put("is_mobile_user", "");
                    } else {
                        result.put("is_mobile_user", id);
                    }
                    result.put("i_name", net.sf.json.JSONObject.fromObject(dpiBean.getBoarding_point()).get("name"));
                    result.put("o_province", net.sf.json.JSONObject.fromObject(dpiBean.getBreakout_point()).get("province"));
                    result.put("o_city", net.sf.json.JSONObject.fromObject(dpiBean.getBreakout_point()).get("city"));
                    result.put("o_name", net.sf.json.JSONObject.fromObject(dpiBean.getBreakout_point()).get("name"));
                    result.put("ini_seats", dpiBean.getInit_seats());
                    result.put("current_seats", dpiBean.getCurrent_seats());
                    if (dpiBean.getFlag() == 0) {
                        result.put("car_color", dpiBean.getCar_color());
                        result.put("car_type", dpiBean.getCar_type());
                    } else {
                        //车辆品牌类型

                        List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(dpiBean.getUser_id());
                        if (travelCardInfos.size() > 0) {
                            UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                            travelCardInfo.getCar_license_number();
                            result.put("car_color", travelCardInfo.getCar_color());
                            result.put("car_type", travelCardInfo.getCar_type());
                        }
                        //车牌号
                    }
                    result.put("name", dpiBean.getUser_name());
                    result.put("user_avatar", dpiBean.getUser_avatar());
                    result.put("remark", dpiBean.getRemark());
                    dataArray.add(result);
                }
                result_json.put("search_data", dataArray);
                json = AppJsonUtils.returnSuccessJsonString(result_json, "搜索成功");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            } else {
                json = AppJsonUtils.returnSuccessJsonString(result_json, "暂无数据");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }
        } else {
            result_json.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result_json, "获取参数有误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
        }
    }
}

