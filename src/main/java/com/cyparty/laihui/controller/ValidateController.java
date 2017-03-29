package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.CarOwnerInfo;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.domain.Popularize;
import com.cyparty.laihui.domain.Popularizing;
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
import java.util.List;

/**
 * Created by zhu on 2016/5/11.
 */
@Controller
@ResponseBody
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class ValidateController {
    @Autowired
    AppDB appDB;
    @Autowired
    OssUtil ossUtil;
    @Autowired
    TestUtils testUtils;
    /***
     * 车型信息模块(乘客实名认证，车主认证)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pc/cartype", method = RequestMethod.POST)
    public ResponseEntity<String> cartype( HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json="";
        try {
            String action=request.getParameter("action");
            switch (action){
                case "getcarbrand":
                    json = AppJsonUtils.returnSuccessJsonString(AppJsonUtils.getCarBrand(appDB), "车辆品牌数据获取成功！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                case "getcartype":
                    String brand_id=request.getParameter("brand_id");
                    json = AppJsonUtils.returnSuccessJsonString(AppJsonUtils.getCarTypeBrand(appDB,brand_id), "车辆类型数据获取成功！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                case "getlicencehead":
                    json = AppJsonUtils.returnSuccessJsonString(AppJsonUtils.getlicensehead(), "车牌号数据获取成功！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                case "getcolor":
                    json = AppJsonUtils.returnSuccessJsonString(AppJsonUtils.getcolor(), "车辆颜色数据获取成功！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }
            result.put("error_code",ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            result.put("error_code",ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }
    /***
     * 身份认证模块(乘客实名认证，车主认证)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/auth/validate", method = RequestMethod.POST)
    public ResponseEntity<String> validate( HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json="";
        try {
            String mobile=request.getParameter("mobile");
            String action=request.getParameter("action");
            boolean is_success;
            String token=null;
            if(request.getParameter("token")!=null){
                token=request.getParameter("token");
            }
            int id= 0;
            if(token!=null){
                id=appDB.getIDByToken(token);
            }else {
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }

            switch (action){
                case "passenger":
                    if(id!=0){
                        String name=request.getParameter("name");
                        String idsn=request.getParameter("idsn");
                        is_success= IDSNValidated.getValidateCode(idsn);
                        if(!is_success){
                            json = AppJsonUtils.returnFailJsonString(result, "身份证号码有误，请重新核对！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }
                        appDB.procedureUpdateUser("create_user",mobile,2,name,idsn,id,token,"",0,"",0);
                        //如果是推广来的就进行下面的操作
                        List<Popularizing> popularizeList = appDB.getPopularize(mobile);
                        if(popularizeList.size()>0){
                            Popularizing popularizing = popularizeList.get(0);
                            String code = popularizing.getPopularize_code();
                            List<Popularize> popularizes = appDB.getPopularized(code);
                            if(popularizes.size()>0){
                                Popularize popular = popularizes.get(0);
                                int user_id = popular.getPopularize_id();
                                int level = popular.getLevel();
                                String popularize_parents_id = popular.getPopularize_parents_id();
                                //重复则不能添加
                                List<Popularize> populars = appDB.getPopular(id);
                                if(populars.size() == 0){
                                    //判断上级等级大小，如果等于5则不再生成推广码
                                    if(level < 5){
                                        String popularize_code = SerialNumberUtil.toSerialNumber(id);
                                        if(level == 0){
                                            appDB.createPopularize(id,user_id,user_id+"",popularize_code,1,1);
                                        }else{
                                            appDB.createPopularize(id,user_id,popularize_parents_id+","+user_id,popularize_code,1,level+1);
                                        }
                                    }
                                }
                            }
                        }

                    }else {
                        result.put("error_code", ErrorCode.getToken_expired());
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                    json = AppJsonUtils.returnSuccessJsonString(result, "实名认证通过！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                case "car_owner":
                    CarOwnerInfo carOwnerInfo=new CarOwnerInfo();
                    String step=request.getParameter("step");
                    if(id!=0){
                        if(step!=null&&step.equals("1")) {
                            is_success=IDSNValidated.getValidateCode(request.getParameter("idsn"));
                            if(is_success){
                                json = AppJsonUtils.returnSuccessJsonString(result, "身份证验证通过！");
                                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                            }else {
                                json = AppJsonUtils.returnFailJsonString(result, "身份证有误！");
                                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                            }
                        }else if(step!=null&&step.equals("2")) {
                            //1.2保存车主信息(创建或者更新)
                            carOwnerInfo.setUser_id(id);
                            carOwnerInfo.setCar_owner_name(request.getParameter("name"));
                            carOwnerInfo.setIdsn(request.getParameter("idsn"));

                            carOwnerInfo.setUser_id(id);
                            carOwnerInfo.setCar_id(request.getParameter("car_no").toUpperCase());
                            carOwnerInfo.setCar_brand(request.getParameter("car_brand"));
                            carOwnerInfo.setCar_type(request.getParameter("car_type"));
                            carOwnerInfo.setCar_color(request.getParameter("car_color"));

                            if(carOwnerInfo.getCar_owner_name()==null||carOwnerInfo.getCar_owner_name().isEmpty()){
                                result.put("error_code",ErrorCode.getParameter_wrong());
                                json = AppJsonUtils.returnFailJsonString(result, "提交失败！");
                                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                            }
                            appDB.procedureValidateCarOwner(0,carOwnerInfo);
                            //如果是推广来的就进行下面的操作
                            List<Popularizing> popularizeList = appDB.getPopularize(mobile);
                            if(popularizeList.size()>0){
                                Popularizing popularizing = popularizeList.get(0);
                                String code = popularizing.getPopularize_code();
                                List<Popularize> popularizes = appDB.getPopularized(code);
                                if(popularizes.size()>0){
                                    Popularize popular = popularizes.get(0);
                                    int user_id = popular.getPopularize_id();
                                    int level = popular.getLevel();
                                    String popularize_parents_id = popular.getPopularize_parents_id();
                                    //重复则不能添加
                                    List<Popularize> populars = appDB.getPopular(id);
                                    if(populars.size() == 0){
                                        if(level < 5){
                                            String popularize_code = SerialNumberUtil.toSerialNumber(id);
                                            if(level == 0){
                                                appDB.createPopularize(id,user_id,user_id+"",popularize_code,1,1);
                                            }else{
                                                appDB.createPopularize(id,user_id,popularize_parents_id+","+user_id,popularize_code,1,level+1);
                                            }
                                        }
                                    }
                                }
                            }
                            // 回写上传的数据
                            JSONObject driver_validate=new JSONObject();
                            driver_validate.put("name",carOwnerInfo.getCar_owner_name());
                            driver_validate.put("idsn",carOwnerInfo.getIdsn());
                            driver_validate.put("car_no",carOwnerInfo.getCar_id());
                            driver_validate.put("car_brand",carOwnerInfo.getCar_brand());
                            driver_validate.put("car_color",carOwnerInfo.getCar_color());
                            driver_validate.put("car_type",carOwnerInfo.getCar_type());

                            result.put("driver_validate",driver_validate);

                            json = AppJsonUtils.returnSuccessJsonString(result, "提交成功！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }else {
                            result.put("error_code",ErrorCode.getParameter_wrong());
                            json = AppJsonUtils.returnFailJsonString(result, "提交失败！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }
                    } else {
                        result.put("error_code", ErrorCode.getToken_expired());
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
            }
            result.put("error_code",ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            e.printStackTrace();
            result.put("error_code",ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }
    /***
     * 校验司机每日发车次数，乘客每日预定次数是否超出系统限制模块（验证码）
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/ticket", method = RequestMethod.POST)
    public ResponseEntity<String> check_times(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            String action = request.getParameter("action");
            String token = null;
            int user_id = 0;
            if (request.getParameter("token") != null) {
                token = request.getParameter("token");
                user_id = appDB.getIDByToken(token);
                if (user_id == 0) {
                    result.put("error_code",ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "无效token！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                }
            }
            String current_time=Utils.getCurrentTime().split(" ")[0]+" 00:00:00";
            String get_ticket_sql="";
            int count=0;
            switch (action) {
                case "driver":
                    get_ticket_sql=" where user_id="+user_id+" and create_time >='"+current_time+"'";
                    count=appDB.getCount("pc_driver_publish_info",get_ticket_sql);
                    if(count<ConfigUtils.getDriver_departure_counts()){
                        result.put("current",count);
                        result.put("total",ConfigUtils.getDriver_departure_counts());
                        result.put("left",ConfigUtils.getDriver_departure_counts()-count);
                        json = AppJsonUtils.returnSuccessJsonString(result, "验证通过！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }else {
                        result.put("current",count);
                        result.put("total",ConfigUtils.getDriver_departure_counts());
                        result.put("left",ConfigUtils.getDriver_departure_counts()-count);
                        json = AppJsonUtils.returnFailJsonString(result, "您今日发车次数已达到每日发车次数上限！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                case "passenger":
                    get_ticket_sql=" where user_id="+user_id+" and create_time >='"+current_time+"'";
                    count=appDB.getCount("pc_passenger_publish_info",get_ticket_sql);
                    if(count<ConfigUtils.getBooking_counts()){
                        result.put("current",count);
                        result.put("total",ConfigUtils.getBooking_counts());
                        result.put("left",ConfigUtils.getBooking_counts()-count);
                        json = AppJsonUtils.returnSuccessJsonString(result, "验证通过！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }else {
                        result.put("current",count);
                        result.put("total",ConfigUtils.getBooking_counts());
                        result.put("left",ConfigUtils.getBooking_counts()-count);
                        json = AppJsonUtils.returnFailJsonString(result, "您今日预定次数已达到每日预定次数上限！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }

            }
            result.put("error_code",ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code",ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

}
