package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import com.cyparty.laihui.utilities.*;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<String> cartype(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            String action = request.getParameter("action");
            switch (action) {
                case "getcarbrand":
                    json = AppJsonUtils.returnSuccessJsonString(AppJsonUtils.getCarBrand(appDB), "车辆品牌数据获取成功！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                case "getcartype":
                    String brand_id = request.getParameter("brand_id");
                    json = AppJsonUtils.returnSuccessJsonString(AppJsonUtils.getCarTypeBrand(appDB, brand_id), "车辆类型数据获取成功！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                case "getlicencehead":
                    json = AppJsonUtils.returnSuccessJsonString(AppJsonUtils.getlicensehead(), "车牌号数据获取成功！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                case "getcolor":
                    json = AppJsonUtils.returnSuccessJsonString(AppJsonUtils.getcolor(), "车辆颜色数据获取成功！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    /***
     * 身份认证模块(乘客实名认证，车主认证)（old）
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/auth/validate", method = RequestMethod.POST)
    public ResponseEntity<String> validate(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            String mobile = request.getParameter("mobile");
            String action = request.getParameter("action");
            boolean is_success;
            String token = null;
            if (request.getParameter("token") != null) {
                token = request.getParameter("token");
                json = AppJsonUtils.returnFailJsonString(result, "为了使你获得更好的服务体验，请下载最新版本！");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }
            int id = 0;
            if (token != null) {
                id = appDB.getIDByToken(token);

            } else {
                result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }

            switch (action) {
                case "passenger":
                    if (id != 0) {
                        String name = request.getParameter("name");
                        String idsn = request.getParameter("idsn");
                        is_success = IDSNValidated.getValidateCode(idsn);
                        if (!is_success) {
                            json = AppJsonUtils.returnFailJsonString(result, "身份证号码有误，请重新核对！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }
                        appDB.procedureUpdateUser("create_user", mobile, 2, name, idsn, id, token, "", 0, "", 0);
                        //如果是推广来的就进行下面的操作
                        List<Popularizing> popularizeList = appDB.getPopularize(mobile);
                        if (popularizeList.size() > 0) {
                            Popularizing popularizing = popularizeList.get(0);
                            String code = popularizing.getPopularize_code();
                            List<Popularize> popularizes = appDB.getPopularized(code);
                            if (popularizes.size() > 0) {
                                Popularize popular = popularizes.get(0);
                                int user_id = popular.getPopularize_id();
                                int level = popular.getLevel();
                                String popularize_parents_id = popular.getPopularize_parents_id();
                                //重复则不能添加
                                List<Popularize> populars = appDB.getPopular(id);
                                if (populars.size() == 0) {
                                    //判断上级等级大小，如果等于5则不再生成推广码
                                    if (level < 5) {
                                        String popularize_code = SerialNumberUtil.toSerialNumber(id);
                                        if (level == 0) {
                                            appDB.createPopularize(id, user_id, user_id + "", user_id, popularize_code, 1, 1);
                                        } else {
                                            String[] strs = popularize_parents_id.split(",");
                                            appDB.createPopularize(id, user_id, popularize_parents_id + "," + user_id, Integer.parseInt(strs[0]), popularize_code, 1, level + 1);
                                        }
                                    }
                                }
                            }
                        }

                    } else {
                        result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                    json = AppJsonUtils.returnSuccessJsonString(result, "实名认证通过！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                case "car_owner":
                    CarOwnerInfo carOwnerInfo = new CarOwnerInfo();
                    String step = request.getParameter("step");
                    if (id != 0) {
                        if (step != null && step.equals("1")) {
                            is_success = IDSNValidated.getValidateCode(request.getParameter("idsn"));
                            if (is_success) {
                                json = AppJsonUtils.returnSuccessJsonString(result, "身份证验证通过！");
                                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                            } else {
                                json = AppJsonUtils.returnFailJsonString(result, "身份证有误！");
                                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                            }
                        } else if (step != null && step.equals("2")) {
                            //1.2保存车主信息(创建或者更新)
                            carOwnerInfo.setUser_id(id);
//                            String user_name = request.getParameter("name");
//                            String user_idsn =request.getParameter("idsn");
                            carOwnerInfo.setCar_owner_name(request.getParameter("name"));
                            carOwnerInfo.setIdsn(request.getParameter("idsn"));

                            carOwnerInfo.setUser_id(id);
                            carOwnerInfo.setCar_id(request.getParameter("car_no").toUpperCase());
                            carOwnerInfo.setCar_brand(request.getParameter("car_brand"));
                            carOwnerInfo.setCar_type(request.getParameter("car_type"));
                            carOwnerInfo.setCar_color(request.getParameter("car_color"));

                            if (carOwnerInfo.getCar_owner_name() == null || carOwnerInfo.getCar_owner_name().isEmpty()) {
                                result.put("error_code", ErrorCode.PARAMETER_WRONG);
                                json = AppJsonUtils.returnFailJsonString(result, "提交失败！");
                                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                            }
                            appDB.procedureValidateCarOwner(0, carOwnerInfo);
                            //如果是推广来的就进行下面的操作
                            List<Popularizing> popularizeList = appDB.getPopularize(mobile);
                            if (popularizeList.size() > 0) {
                                Popularizing popularizing = popularizeList.get(0);
                                String code = popularizing.getPopularize_code();
                                List<Popularize> popularizes = appDB.getPopularized(code);
                                if (popularizes.size() > 0) {
                                    Popularize popular = popularizes.get(0);
                                    int user_id = popular.getPopularize_id();
                                    int level = popular.getLevel();
                                    String popularize_parents_id = popular.getPopularize_parents_id();
                                    //重复则不能添加
                                    List<Popularize> populars = appDB.getPopular(id);
                                    if (populars.size() == 0) {
                                        if (level < 5) {
                                            String popularize_code = SerialNumberUtil.toSerialNumber(id);
                                            if (level == 0) {
                                                appDB.createPopularize(id, user_id, user_id + "", user_id, popularize_code, 1, 1);
                                            } else {
                                                String[] strs = popularize_parents_id.split(",");
                                                appDB.createPopularize(id, user_id, popularize_parents_id + "," + user_id, Integer.parseInt(strs[0]), popularize_code, 1, level + 1);
                                            }
                                        }
                                    }
                                }
                            }
                            // 回写上传的数据
                            JSONObject driver_validate = new JSONObject();
                            driver_validate.put("name", carOwnerInfo.getCar_owner_name());
                            driver_validate.put("idsn", carOwnerInfo.getIdsn());
                            driver_validate.put("car_no", carOwnerInfo.getCar_id());
                            driver_validate.put("car_brand", carOwnerInfo.getCar_brand());
                            driver_validate.put("car_color", carOwnerInfo.getCar_color());
                            driver_validate.put("car_type", carOwnerInfo.getCar_type());

                            result.put("driver_validate", driver_validate);

                            json = AppJsonUtils.returnSuccessJsonString(result, "提交成功！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            result.put("error_code", ErrorCode.PARAMETER_WRONG);
                            json = AppJsonUtils.returnFailJsonString(result, "提交失败！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }
                    } else {
                        result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
            }
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
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
        responseHeaders.set("Access-Control-Allow-Origin", "*");
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
                    result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                    json = AppJsonUtils.returnFailJsonString(result, "无效token！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                }
            }
            String current_time = Utils.getCurrentTime().split(" ")[0] + " 00:00:00";
            String get_ticket_sql = "";
            int count = 0;
            switch (action) {
                case "driver":
                    get_ticket_sql = " where user_id=" + user_id + " and create_time >='" + current_time + "'";
                    count = appDB.getCount("pc_driver_publish_info", get_ticket_sql);
                    if (count < ConfigUtils.DRIVER_DEPARTURE_COUNTS) {
                        result.put("current", count);
                        result.put("total", ConfigUtils.DRIVER_DEPARTURE_COUNTS);
                        result.put("left", ConfigUtils.DRIVER_DEPARTURE_COUNTS - count);
                        json = AppJsonUtils.returnSuccessJsonString(result, "验证通过！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("current", count);
                        result.put("total", ConfigUtils.DRIVER_DEPARTURE_COUNTS);
                        result.put("left", ConfigUtils.DRIVER_DEPARTURE_COUNTS - count);
                        json = AppJsonUtils.returnFailJsonString(result, "您今日发车次数已达到每日发车次数上限！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                case "passenger":
                    get_ticket_sql = " where user_id=" + user_id + " and create_time >='" + current_time + "'";
                    count = appDB.getCount("pc_passenger_publish_info", get_ticket_sql);
                    if (count < ConfigUtils.BOOKING_COUNTS) {
                        result.put("current", count);
                        result.put("total", ConfigUtils.BOOKING_COUNTS);
                        result.put("left", ConfigUtils.BOOKING_COUNTS - count);
                        json = AppJsonUtils.returnSuccessJsonString(result, "验证通过！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("current", count);
                        result.put("total", ConfigUtils.BOOKING_COUNTS);
                        result.put("left", ConfigUtils.BOOKING_COUNTS - count);
                        json = AppJsonUtils.returnFailJsonString(result, "您今日预定次数已达到每日预定次数上限！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
            }
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    /***
     * 身份认证模块(实名认证)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/validate", method = RequestMethod.POST)
    public ResponseEntity<String> userValidate(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json = "";
        try {
//            String mobile=request.getParameter("mobile");
            boolean is_success;
            String token = null;
            if (request.getParameter("token") != null) {
                token = request.getParameter("token");
            }
            int id = 0;
            if (token != null) {
                id = appDB.getIDByToken(token);
            } else {
                result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
            if (id != 0) {
                String name = request.getParameter("name");
                String idsn = request.getParameter("idsn");
                if (idsn == null || idsn.length() != 172 && idsn.length() != 175 || null == name) {
                    json = AppJsonUtils.returnFailJsonString(result, "输入信息不正确，请重新输入！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
                //身份证号解密
                String carId = RSAUtils.getEncryptor(idsn);
                String car_id = carId.substring(0, carId.length() - 5);
                IdcardValidator idcardValidator = new IdcardValidator();
                if (!idcardValidator.isValidate18Idcard(car_id)) {
                    json = AppJsonUtils.returnFailJsonString(result, "身份证号格式不正确");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                }
                List<User> userList = appDB.getUserList(" where user_idsn ='"+car_id+"' and is_validated =1 and u_flag =1");
                if(userList.size()>0){
                    json = AppJsonUtils.returnFailJsonString(result, "该身份证号已经认证！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
                //测试
//                if (car_id.equals("411422199401032416")){
//                    String mobile ="15738961936";
//                    appDB.procedureUpdateUser("create_user", mobile, 2, name, car_id, id, token, "", 0, "", 0);
//                    appDB.update("pc_user"," set u_flag =1 where _id="+id);
//                    //如果是推广来的就进行下面的操作
//                    List<Popularizing> popularizeList = appDB.getPopularize(mobile);
//                    if (popularizeList.size() > 0) {
//                        Popularizing popularizing = popularizeList.get(0);
//                        String code = popularizing.getPopularize_code();
//                        List<Popularize> popularizes = appDB.getPopularized(code);
//                        if (popularizes.size() > 0) {
//                            Popularize popular = popularizes.get(0);
//                            int user_id = popular.getPopularize_id();
//                            int level = popular.getLevel();
//                            String popularize_parents_id = popular.getPopularize_parents_id();
//                            //重复则不能添加
//                            List<Popularize> populars = appDB.getPopular(id);
//                            if (populars.size() == 0) {
//                                //判断上级等级大小，如果等于5则不再生成推广码
//                                if (level < 5) {
//                                    String popularize_code = SerialNumberUtil.toSerialNumber(id);
//                                    if (level == 0) {
//                                        appDB.createPopularize(id, user_id, user_id + "", user_id, popularize_code, 1, 1);
//                                        String where = " set p_id ="+user_id+" where _id="+id;
//                                        appDB.update("pc_user",where);
//                                    } else {
//                                        String[] strs = popularize_parents_id.split(",");
//                                        appDB.createPopularize(id, user_id, popularize_parents_id + "," + user_id, Integer.parseInt(strs[0]), popularize_code, 1, level + 1);
//                                        String where = " set p_id ="+Integer.parseInt(strs[0])+" where _id="+id;
//                                        appDB.update("pc_user",where);
//                                    }
//                                }
//                            }
//                        }
//                    }else{
//                        //如果是全民推广推广来的用户is_reg 1为全民推广的用户，0 为非全民推广的用户
//                        List<Campaign> campaigns = appDB.getCampaign(" where be_popularized_mobile ='"+mobile+"'");
//                        if(campaigns.size()>0){
//                            Campaign campaign = campaigns.get(0);
//                            if(0==campaign.getIs_reg()){
//                                String startTime = Utils.getCurrentTime();
//                                appDB.update("pc_campaign"," set is_reg =1 ,reg_time ='"+startTime+"' where be_popularized_mobile ='"+mobile+"'");
//                                appDB.update("pc_user", " set p_id="+campaign.getUser_id()+" where is_validated =1 and u_flag=1 and user_mobile='"+mobile+"'");
//                                json = AppJsonUtils.returnSuccessJsonString(result, "实名认证通过！");
//                                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
//                            }else{
//                                result.put("msg", "您已经全民推广用户了");
//                                json = AppJsonUtils.returnFailJsonString(result, "您已经全民推广用户了！");
//                                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
//                            }
//                        }
//                    }
//                }
                //调用阿里Api验证身份信息
                String user = ValidateUtils.getUrl(car_id,name);
                String body = Utils.getJsonObject(user, "showapi_res_body");
                String userCode = Utils.getJsonObject(body, "code");
                if ("0".equals(userCode)) {
                    is_success = IDSNValidated.getValidateCode(car_id);
                    if (!is_success) {
                        json = AppJsonUtils.returnFailJsonString(result, "身份证号码有误，请重新核对！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                    List<User> users = appDB.getUserList("  where _id =" + id);
                    if (users.size() < 1) {
                        json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
                    }
                    String mobile = users.get(0).getUser_mobile();
                    appDB.procedureUpdateUser("create_user", mobile, 2, name, car_id, id, token, "", 0, "", 0);
                    appDB.update("pc_user"," set u_flag =1 where _id="+id);
                    //如果是推广来的就进行下面的操作
                    List<Popularizing> popularizeList = appDB.getPopularize(mobile);
                    if (popularizeList.size() > 0) {
                        Popularizing popularizing = popularizeList.get(0);
                        String code = popularizing.getPopularize_code();
                        List<Popularize> popularizes = appDB.getPopularized(code);
                        if (popularizes.size() > 0) {
                            Popularize popular = popularizes.get(0);
                            int user_id = popular.getPopularize_id();
                            int level = popular.getLevel();
                            String popularize_parents_id = popular.getPopularize_parents_id();
                            //重复则不能添加
                            List<Popularize> populars = appDB.getPopular(id);
                            if (populars.size() == 0) {
                                //判断上级等级大小，如果等于5则不再生成推广码
                                if (level < 5) {
                                    String popularize_code = SerialNumberUtil.toSerialNumber(id);
                                    if (level == 0) {
                                        appDB.createPopularize(id, user_id, user_id + "", user_id, popularize_code, 1, 1);
                                        String where = " set p_id ="+user_id+" where _id="+id;
                                        appDB.update("pc_user",where);
                                    } else {
                                        String[] strs = popularize_parents_id.split(",");
                                        appDB.createPopularize(id, user_id, popularize_parents_id + "," + user_id, Integer.parseInt(strs[0]), popularize_code, 1, level + 1);
                                        String where = " set p_id ="+Integer.parseInt(strs[0])+" where _id="+id;
                                        appDB.update("pc_user",where);
                                    }
                                }
                            }
                        }
                    }else{
                        //如果是全民推广推广来的用户is_reg 1为全民推广的用户，0 为非全民推广的用户
                        List<Campaign> campaigns = appDB.getCampaign(" where be_popularized_mobile ='"+mobile+"'");
                        if(campaigns.size()>0){
                            Campaign campaign = campaigns.get(0);
                            if(0==campaign.getIs_reg()){
                                String startTime = Utils.getCurrentTime();
                                appDB.update("pc_campaign"," set is_reg =1 ,reg_time ='"+startTime+"' where be_popularized_mobile ='"+mobile+"'");
                                appDB.update("pc_user", " set p_id="+campaign.getUser_id()+" where is_validated =1 and u_flag=1 and user_mobile='"+mobile+"'");
                            }else{
                                result.put("msg", "您已经全民推广用户了");
                                json = AppJsonUtils.returnFailJsonString(result, "您已经全民推广用户了！");
                                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                            }
                        }
                    }
                    result.put("data", body);
                    json = AppJsonUtils.returnSuccessJsonString(result, "实名认证通过！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                } else if ("2".equals(userCode)) {
                    result.put("data", body);
                    json = AppJsonUtils.returnFailJsonString(result, "身份证号码有误，请重新核对！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                }
                result.put("data", body);
                json = AppJsonUtils.returnFailJsonString(result, "身份证与姓名不匹配!");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            } else {
                result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    /***
     * 车主驾驶证信息提交
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/driverLicense/validate", method = RequestMethod.POST)
    public ResponseEntity<String> UserDriverLicenseInfoValidate(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json = "";
        String token = request.getParameter("token");
        String driver_name = request.getParameter("driver_name");
        String driver_license_number = request.getParameter("driver_license_number");
        String first_issue_date = request.getParameter("first_issue_date");
        String allow_car_type = request.getParameter("allow_car_type");
        String effective_date_start = request.getParameter("effective_date_start");
        String effective_date_end = request.getParameter("effective_date_end");
        //String photo = request.getParameter("driver_license_photo");
        if (StringUtil.isBlank(token) || "null".equals(token)) {
            result.put("error_code", ErrorCode.TOKEN_EXPIRED);
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (StringUtil.isBlank(driver_name) || "null".equals(driver_name)) {
            json = AppJsonUtils.returnFailJsonString(result, "信息不完整，请核实后提交！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (StringUtil.isBlank(first_issue_date) || "null".equals(first_issue_date)) {
            json = AppJsonUtils.returnFailJsonString(result, "信息不完整，请核实后提交！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (StringUtil.isBlank(driver_license_number) || "null".equals(driver_license_number)) {
            json = AppJsonUtils.returnFailJsonString(result, "信息不完整，请核实后提交！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (StringUtil.isBlank(allow_car_type) || "null".equals(allow_car_type)) {
            json = AppJsonUtils.returnFailJsonString(result, "信息不完整，请核实后提交！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (StringUtil.isBlank(effective_date_start) || "null".equals(effective_date_start)) {
            json = AppJsonUtils.returnFailJsonString(result, "信息不完整，请核实后提交！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (StringUtil.isBlank(effective_date_end) || "null".equals(effective_date_end)) {
            json = AppJsonUtils.returnFailJsonString(result, "信息不完整，请核实后提交！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else {
            int user_id = appDB.getIDByToken(token);
            if (user_id > 0) {
                String image_oss = "";
                String filePath = Utils.fileImgUpload("driver_license_photo", request);
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
                if (image_oss != null) {
                    List<UserDriverLicenseInfo> userDriverLicenseInfos = appDB.getDriverLicense(user_id);
                    if(userDriverLicenseInfos.size()>0){
                        UserDriverLicenseInfo user = userDriverLicenseInfos.get(0);
                        String is_enable = user.getIs_enable();
                        if("2".equals(is_enable)){
                            String where =" set driver_name='"+driver_name+"',driver_license_number='"+driver_license_number+"',first_issue_date='"+first_issue_date+"',allow_car_type='"+allow_car_type+"',effective_date_start='"+effective_date_start+"',effective_date_end='"+effective_date_end+"',driver_license_photo='"+image_oss+"',is_enable='"+1+"' where user_id='"+user_id+"'";
                            appDB.update("pc_user_driver_license_info",where);
                            List<UserTravelCardInfo> userTravelCardInfos = appDB.getTravelCard(user_id);
                            if(userTravelCardInfos.size()>0){
                                UserTravelCardInfo userTravelCardInfo=userTravelCardInfos.get(0);
                                if("1".equals(userTravelCardInfo.getIs_enable())){
                                    appDB.update("pc_user"," set is_car_owner =2 where _id ="+user_id);
                                }
                            }
                            json = AppJsonUtils.returnSuccessJsonString(result, "信息提交成功，系统正在审核！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }else if("3".equals(is_enable)){
                            json = AppJsonUtils.returnSuccessJsonString(result, "已经认证，更改无效！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }else if("1".equals(is_enable)) {
                            json = AppJsonUtils.returnSuccessJsonString(result, "系统正在审核，请耐心等待！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }
                    }
                    boolean is_true = false;
                    is_true = appDB.createDriverLicense(user_id, driver_name, driver_license_number, first_issue_date, allow_car_type, effective_date_start, effective_date_end, image_oss, "1");
                    List<UserTravelCardInfo> userTravelCardInfos = appDB.getTravelCard(user_id);
                    if(userTravelCardInfos.size()>0){
                        UserTravelCardInfo userTravelCardInfo=userTravelCardInfos.get(0);
                        if("1".equals(userTravelCardInfo.getIs_enable())){
                            appDB.update("pc_user"," set is_car_owner =2 where _id ="+user_id);
                        }
                    }
                    if (is_true ) {
                        json = AppJsonUtils.returnSuccessJsonString(result, "信息提交成功，系统正在审核！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        json = AppJsonUtils.returnFailJsonString(result, "信息提交失败，请核对信息后提交！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                } else {
                    json = AppJsonUtils.returnFailJsonString(result, "信息提交失败，请核对信息后提交！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                }
            } else {
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
            }
        }
    }

    /***
     * 车主行驶证信息提交
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/travelCard/validate", method = RequestMethod.POST)
    public ResponseEntity<String> travelCardValidate(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json = "";
        String token = request.getParameter("token");
        String car_license_number = request.getParameter("car_license_number");
        String car_color = request.getParameter("car_color");
        String registration_date = request.getParameter("registration_date");
        String car_type = request.getParameter("car_type");
        String vehicle_owner_name = request.getParameter("vehicle_owner_name");
//        String photo = request.getParameter("travel_license_photo");
        if (StringUtil.isBlank(token) || "null".equals(token)) {
            result.put("error_code", ErrorCode.TOKEN_EXPIRED);
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (StringUtil.isBlank(car_license_number) || "null".equals(car_license_number)) {
            json = AppJsonUtils.returnFailJsonString(result, "信息不完整，请核实后提交！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (StringUtil.isBlank(car_color) || "null".equals(car_color)) {
            json = AppJsonUtils.returnFailJsonString(result, "信息不完整，请核实后提交！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (StringUtil.isBlank(registration_date) || "null".equals(registration_date)) {
            json = AppJsonUtils.returnFailJsonString(result, "信息不完整，请核实后提交！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (StringUtil.isBlank(car_type) || "null".equals(car_type)) {
            json = AppJsonUtils.returnFailJsonString(result, "信息不完整，请核实后提交！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } else if (StringUtil.isBlank(vehicle_owner_name) || "null".equals(vehicle_owner_name)) {
            json = AppJsonUtils.returnFailJsonString(result, "信息不完整，请核实后提交！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }   else {
            int user_id = appDB.getIDByToken(token);
            if (user_id > 0) {
                String image_oss = "";
                String filePath = Utils.fileImgUpload("travel_license_photo", request);
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
                if (image_oss != null) {
                    List<UserTravelCardInfo> userTravelCardInfos = appDB.getTravelCard(user_id);
                    if(userTravelCardInfos.size()>0){
                        UserTravelCardInfo user = userTravelCardInfos.get(0);
                        String is_enable = user.getIs_enable();
                        if("2".equals(is_enable)){
                            String where =" set car_license_number='"+car_license_number+"',car_color='"+car_color+"',registration_date='"+registration_date+"',car_type='"+car_type+"',vehicle_owner_name='"+vehicle_owner_name+"',travel_license_photo='"+image_oss+"',is_enable='"+1+"' where user_id='"+user_id+"'";
                            appDB.update("pc_user_travel_card_info",where);
                            List<UserDriverLicenseInfo> userDriverLicenseInfos = appDB.getDriverLicense(user_id);
                            if(userDriverLicenseInfos.size()>0){
                                UserDriverLicenseInfo userDriverLicenseInfo=userDriverLicenseInfos.get(0);
                                if("1".equals(userDriverLicenseInfo.getIs_enable())){
                                    appDB.update("pc_user"," set is_car_owner =2 where _id ="+user_id);
                                }
                            }
                            json = AppJsonUtils.returnSuccessJsonString(result, "信息提交成功，系统正在审核！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }else if("3".equals(is_enable)){
                            json = AppJsonUtils.returnSuccessJsonString(result, "已经认证，更改无效！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }else if("1".equals(is_enable)) {
                            json = AppJsonUtils.returnSuccessJsonString(result, "系统正在审核，请耐心等待！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }
                    }
                    boolean is_true = false;
                    is_true = appDB.createTravelCard(user_id, car_license_number, car_color,  car_type, registration_date, vehicle_owner_name, image_oss, "1");
                    List<UserDriverLicenseInfo> userDriverLicenseInfos = appDB.getDriverLicense(user_id);
                    if(userDriverLicenseInfos.size()>0){
                        UserDriverLicenseInfo userDriverLicenseInfo=userDriverLicenseInfos.get(0);
                        if("1".equals(userDriverLicenseInfo.getIs_enable())){
                            appDB.update("pc_user"," set is_car_owner =2 where _id ="+user_id);
                        }
                    }
                    if (is_true) {
                        json = AppJsonUtils.returnSuccessJsonString(result, "信息提交成功，系统正在审核！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        json = AppJsonUtils.returnFailJsonString(result, "信息提交失败，请核对信息后提交！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                } else {
                    json = AppJsonUtils.returnFailJsonString(result, "信息提交失败，请核对信息后提交！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                }
            } else {
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
            }
        }
    }

    /***
     * 车主驾驶证认证状态查询
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/driverValidate/status", method = RequestMethod.POST)
    public ResponseEntity<String> driverLicenseStatus(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json = "";
        String where = "";
        JSONObject user = new JSONObject();
        JSONObject driver = new JSONObject();
        String token = request.getParameter("token");
        if(StringUtil.isBlank(token) || "null".equals(token)){
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
        int user_id = appDB.getIDByToken(token);
        if (user_id > 0) {
            where = " where _id="+user_id;
            User users = appDB.getUserList(where).get(0);
            List<UserDriverLicenseInfo> userDriverLicenseInfoList = appDB.getDriverLicense(user_id);
            List<UserTravelCardInfo> userTravelCardInfos= appDB.getTravelCard(user_id);
            if(userDriverLicenseInfoList.size()>0&& userTravelCardInfos.size()>0){
                UserDriverLicenseInfo userInfo = userDriverLicenseInfoList.get(0);
                UserTravelCardInfo driverIfo = userTravelCardInfos.get(0);
                user.put("driver_name",users.getUser_name());
                user.put("driver_license_number",users.getUser_idsn());
                user.put("first_issue_date",userInfo.getFirst_issue_date());
                user.put("allow_car_type",userInfo.getAllow_car_type());
                user.put("effective_date_start",userInfo.getEffective_date_start());
                user.put("effective_date_end",userInfo.getEffective_date_end());
                user.put("photo_url",userInfo.getDriver_license_photo());
                user.put("status",userInfo.getIs_enable());
                result.put("driverLicense",user);
                //行驶证信息
                driver.put("car_license_number",driverIfo.getCar_license_number());
                driver.put("car_color",driverIfo.getCar_color());
                driver.put("car_type",driverIfo.getCar_type());
                driver.put("registration_date",driverIfo.getRegistration_date());
                driver.put("vehicle_owner_name",driverIfo.getVehicle_owner_name());
                driver.put("photo_url",driverIfo.getTravel_license_photo());
                driver.put("status",driverIfo.getIs_enable());
                result.put("travelCard",driver);
                if(userInfo.getIs_enable().equals("3")&&driverIfo.getIs_enable().equals("3")){
                    result.put("status","3");
                }else if(userInfo.getIs_enable().equals("0")||driverIfo.getIs_enable().equals("0")){
                    result.put("status","0");
                }else if(userInfo.getIs_enable().equals("1")||driverIfo.getIs_enable().equals("1")){
                    result.put("status","1");
                }else{
                    result.put("status","2");
                }
                json = AppJsonUtils.returnSuccessJsonString(result, "查询消息成功！");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }else if(userDriverLicenseInfoList.size()>0&&userTravelCardInfos.size()==0) {
                UserDriverLicenseInfo userInfo = userDriverLicenseInfoList.get(0);
                user.put("driver_name", users.getUser_name());
                user.put("driver_license_number", users.getUser_idsn());
                user.put("first_issue_date", userInfo.getFirst_issue_date());
                user.put("allow_car_type", userInfo.getAllow_car_type());
                user.put("effective_date_start", userInfo.getEffective_date_start());
                user.put("effective_date_end", userInfo.getEffective_date_end());
                user.put("photo_url", userInfo.getDriver_license_photo());
                user.put("status", userInfo.getIs_enable());
                result.put("driverLicense", user);
                //行驶证信息
                driver.put("car_license_number", "");
                driver.put("car_color", "");
                driver.put("car_type", "");
                driver.put("registration_date", "");
                driver.put("vehicle_owner_name", "");
                driver.put("photo_url", "");
                driver.put("status", "0");
                result.put("travelCard", driver);
                result.put("status","0");
                json = AppJsonUtils.returnSuccessJsonString(result, "查询消息成功！");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }else if(userDriverLicenseInfoList.size()==0&&userTravelCardInfos.size()>0){
                UserTravelCardInfo driverIfo = userTravelCardInfos.get(0);
                user.put("driver_name",users.getUser_name());
                user.put("driver_license_number",users.getUser_idsn());
                user.put("first_issue_date","");
                user.put("allow_car_type","");
                user.put("effective_date_start","");
                user.put("effective_date_end","");
                user.put("photo_url","");
                user.put("status","0");
                result.put("driverLicense",user);
                //行驶证信息
                driver.put("car_license_number",driverIfo.getCar_license_number());
                driver.put("car_color",driverIfo.getCar_color());
                driver.put("car_type",driverIfo.getCar_type());
                driver.put("registration_date",driverIfo.getRegistration_date());
                driver.put("vehicle_owner_name",driverIfo.getVehicle_owner_name());
                driver.put("photo_url",driverIfo.getTravel_license_photo());
                driver.put("status",driverIfo.getIs_enable());
                result.put("travelCard",driver);
                result.put("status","0");
                json = AppJsonUtils.returnSuccessJsonString(result, "查询消息成功！");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }else{
                user.put("driver_name",users.getUser_name());
                user.put("driver_license_number",users.getUser_idsn());
                user.put("first_issue_date","");
                user.put("allow_car_type","");
                user.put("effective_date_start","");
                user.put("effective_date_end","");
                user.put("photo_url","");
                user.put("status","0");
                result.put("driverLicense",user);
                //行驶证信息
                driver.put("car_license_number","");
                driver.put("car_color","");
                driver.put("car_type","");
                driver.put("registration_date","");
                driver.put("vehicle_owner_name","");
                driver.put("photo_url","");
                driver.put("status","0");
                result.put("travelCard",driver);
                result.put("status","0");
                json = AppJsonUtils.returnFailJsonString(result, "暂未查询到您的车主认证信息！");
                return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
            }
        }else{
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

}