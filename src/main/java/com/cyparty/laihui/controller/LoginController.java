package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import com.cyparty.laihui.utilities.*;
import com.cyparty.laihui.utilities.SMSUtils.SmsWebApiKit;
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
public class LoginController {
    @Autowired
    AppDB appDB;
    @Autowired
    OssUtil ossUtil;

    /***
     * 登陆模块（验证码）
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<String> reg(HttpServletRequest request) {
        HttpHeaders responseHeaders = AjaxOriginUtil.getHttpHeaders();
        JSONObject result = new JSONObject();
        String json = "";
        List<User> userList1 = new ArrayList<>();
        String where = "";
        try {
            String mobile = request.getParameter("mobile");
            String action = request.getParameter("action");
            //验证码
            String code = null;
            String token = null;
            if (request.getParameter("token") != null) {
                token = request.getParameter("token");
            }
            int id = 0;
            switch (action) {
                case "sms":
                    //判断是否是已注册手机号
                    where = " where user_mobile=" + mobile;
                    userList1 = appDB.getUserList(where);
                    if (userList1.size() == 0) {
                        result.put("error_code", ErrorCode.SMS_TIMES_LIMIT);
                        json = AppJsonUtils.returnFailJsonString(result, "为了使您拥有更好的体验，请到应用宝市场下载最新版本");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                    int total = appDB.getSendCodeTimes(mobile);
                    if (total <= 5) {
                        //发送验证码
                        code = Utils.sendCodeMessage(mobile);
                    } else {
                        result.put("error_code", ErrorCode.SMS_TIMES_LIMIT);
                        json = AppJsonUtils.returnFailJsonString(result, "发送验证码过于频繁，请稍后重试！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                    if (code != null) {
                        //保存记录
                        appDB.createSMS(mobile, code);
                        json = AppJsonUtils.returnSuccessJsonString(result, "验证码发送成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.SMS_SEND_FAILED);
                        json = AppJsonUtils.returnFailJsonString(result, "验证码发送失败，请校验您输入的手机号是否正确！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                case "new_sms":
                    //手机号加密后的字符串
                    //String my_mobile = "O0jD+jlqkxLcqfOfBLNacHCzGLkFcTbCMlZVvTlknilzaPiass+DoumWBJHvbd1smn6xEmaajUvqfPYmBwK4ufXM+Z8vtaIXjOtb0UdIXZpeQJwSuyoWiaKDfWL3NyHmlGvT+RR6CvRKSFlWo3YOp0MS2i8/MVi3dfZ0Q0jBFdk=";
                    String my_mobile = request.getParameter("my_mobile");
                    if (my_mobile == null || my_mobile.length() != 172 && my_mobile.length() != 175) {
                        result.put("error_code", ErrorCode.SMS_SEND_FAILED);
                        json = AppJsonUtils.returnFailJsonString(result, "验证码发送失败，请校验您输入的手机号是否正确！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    //解密和截取手机号
                    try {
                        mobile = RSAUtils.getEncryptor(my_mobile).substring(0, 11);
                        //判断是否是已注册手机号
                        where = " where user_mobile=" + mobile;
                        userList1 = appDB.getUserList(where);
                        if (userList1.size() == 0) {
                            result.put("error_code", ErrorCode.SMS_TIMES_LIMIT);
                            json = AppJsonUtils.returnFailJsonString(result, "为了使您拥有更好的体验，请到应用宝市场下载最新版本");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }
                    } catch (Exception e) {
                        result.put("error_code", ErrorCode.PARAMETER_WRONG);
                        json = AppJsonUtils.returnFailJsonString(result, "为了使您拥有更好的体验，请到应用宝市场下载最新版本");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    int total1 = appDB.getSendCodeTimes(mobile);
                    if (total1 <= 5) {
                        //发送验证码
                        code = Utils.sendCodeMessage(mobile);
                    } else {
                        result.put("error_code", ErrorCode.SMS_TIMES_LIMIT);
                        json = AppJsonUtils.returnFailJsonString(result, "发送验证码过于频繁，请稍后重试！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                    if (code != null) {
                        //保存记录
                        appDB.createSMS(mobile, code);
                        json = AppJsonUtils.returnSuccessJsonString(result, "验证码发送成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.SMS_SEND_FAILED);
                        json = AppJsonUtils.returnFailJsonString(result, "验证码发送失败，请校验您输入的手机号是否正确！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    //聚合版短信检查
                case "sms_check":
                    String addr = Utils.getIP(request);
                    String ip_location;
                    try {
                        ip_location = GetIPLocation.getIpLocation(addr, 0);
                    } catch (Exception e) {
                        ip_location = "查询失败";
                        //e.printStackTrace();
                    }
                    code = request.getParameter("code");
                    where = " where mobile='" + mobile + "' ";
                    List<Code> codeList = appDB.getSMS(where);
                    if (codeList.size() > 0) {
                        //得到此次短信的发送记录
                        Code now_code = codeList.get(0);
                        if (now_code.getCode().equals(code)) {
                            //创建该用户
                            where = " where user_mobile='" + mobile + "' ";
                            List<User> userList = appDB.getUserList(where);
                            if (userList.size() > 0) {
                                //update
                                id = userList.get(0).getUser_id();
                                //通过用户id生成身份令牌
                                token = IDTransform.transformID(id);
                                appDB.procedureUpdateUser("create_user", mobile, 1, "", "", id, token, addr, 0, ip_location, 0);
                            } else {
                                //create
                                String now_source = request.getParameter("source");
                                int source = 0;
                                if (now_source != null && !now_source.isEmpty() && now_source.equals("iOS")) {
                                    source = 1;
                                }
                                //查询是否是通过专业推广过来的用户
                                int p_id = 0;

                                List<Popularize> PopularizeList = appDB.getPopular(id);
                                if (PopularizeList.size() > 0) {
                                    if (PopularizeList.get(0).getLevel() > 0) {
                                        p_id = Integer.parseInt(PopularizeList.get(0).getPopularize_parents_id().split(",")[0]);
                                    }
                                }
                                appDB.procedureUpdateUser("create_user", mobile, 0, "", "", id, "", addr, source, ip_location, p_id);
                                id = appDB.getUserList(where).get(0).getUser_id();
                                token = IDTransform.transformID(id);
                                appDB.createUserToken(token, id);
                            }
                            result.put("token", token);
                            json = AppJsonUtils.returnSuccessJsonString(result, "验证码正确！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            result.put("error_code", ErrorCode.SMS_CHECKED_FAILED);
                            json = AppJsonUtils.returnFailJsonString(result, "验证码错误，请核对您的校验码！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                    } else {
                        result.put("error_code", ErrorCode.SMS_CHECKED_FAILED);
                        json = AppJsonUtils.returnFailJsonString(result, "请确认是否收到验证码！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    //MOB版短信检查
                case "mob_sms_check":
                    String status = "";
                    String addr1 = Utils.getIP(request);
                    String ip_location1;
                    try {
                        ip_location1 = GetIPLocation.getIpLocation(addr1, 0);
                    } catch (Exception e) {
                        ip_location1 = "查询失败";
                        //e.printStackTrace();
                    }
                    code = request.getParameter("code");
                    //测试手机号判断
                    if (TestConfigUtils.getMobile(mobile)) {
                        status = "200";
                        if (!code.equals("0603")) {
                            result.put("error_code", ErrorCode.SMS_CHECKED_FAILED);
                            json = AppJsonUtils.returnFailJsonString(result, "验证码错误，请核对您的校验码！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                        //创建该用户
                        where = " where user_mobile='" + mobile + "' ";
                        List<User> userList = appDB.getUserList(where);
                        if (userList.size() > 0) {
                            //update
                            id = userList.get(0).getUser_id();
                            //通过用户id生成身份令牌
                            token = IDTransform.transformID(id);
                            appDB.procedureUpdateUser("create_user", mobile, 1, "", "", id, token, addr1, 0, ip_location1, 0);
                            result.put("token", token);
                            json = AppJsonUtils.returnSuccessJsonString(result, "验证码正确！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                    } else {
                        //得到返回状态
                        status = SmsWebApiKit.getInstance().checkcode(mobile, "86", code);
                    }
                    if (status.equals("200")) {
                        //创建该用户
                        where = " where user_mobile='" + mobile + "' ";
                        List<User> userList = appDB.getUserList(where);
                        if (userList.size() > 0) {
                            //update
                            id = userList.get(0).getUser_id();
                            //通过用户id生成身份令牌
                            token = IDTransform.transformID(id);
                            appDB.procedureUpdateUser("create_user", mobile, 1, "", "", id, token, addr1, 0, ip_location1, 0);
                        } else {
                            //create
                            String now_source = request.getParameter("source");
                            int source = 0;
                            if (now_source != null && !now_source.isEmpty() && now_source.equals("iOS")) {
                                source = 1;
                            }
                            //查询是否是通过专业推广过来的用户
                            int p_id = 0;

                            List<Popularize> PopularizeList = appDB.getPopular(id);
                            if (PopularizeList.size() > 0) {
                                if (PopularizeList.get(0).getLevel() > 0) {
                                    p_id = Integer.parseInt(PopularizeList.get(0).getPopularize_parents_id().split(",")[0]);
                                }
                            }
                            appDB.procedureUpdateUser("create_user", mobile, 0, "", "", id, "", addr1, source, ip_location1, p_id);
                            id = appDB.getUserList(where).get(0).getUser_id();
                            token = IDTransform.transformID(id);
                            appDB.createUserToken(token, id);
                        }
                        result.put("token", token);

                        json = AppJsonUtils.returnSuccessJsonString(result, "验证码正确！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else if (status.equals("466")) {
                        result.put("error_code", ErrorCode.SMS_TIMES_LIMIT);
                        json = AppJsonUtils.returnFailJsonString(result, "验证码为空！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.SMS_CHECKED_FAILED);
                        json = AppJsonUtils.returnFailJsonString(result, "验证码错误，请核对您的校验码！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }


                    //检查token
                case "check_token":
                    if (token != null) {
                        int user_id = appDB.getIDByToken(token);
                        if (user_id != 0) {
                            json = AppJsonUtils.returnSuccessJsonString(result, "token验证通过！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                            json = AppJsonUtils.returnFailJsonString(result, "token失效！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                    } else {
                        result.put("error_code", ErrorCode.PARAMETER_WRONG);
                        json = AppJsonUtils.returnFailJsonString(result, "参数有误！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
            }
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    /***
     * 用户相关数据(头像，拼车次数，乘客实名认证，车主认证情况)
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/auth/info", method = RequestMethod.POST)
    public ResponseEntity<String> info(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            String action = request.getParameter("action");
            String token = null;
            int id = 0;
            if (request.getParameter("token") != null) {
                token = request.getParameter("token");
                id = appDB.getIDByToken(token);
            } else {
                result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
            switch (action) {
                //添加头像
                case "add_avatar":
                    //新建
                    if (id > 0) {
                        String image_oss = "";
                        String filePath = Utils.fileImgUpload("photo", request);
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
                            String update_sql = " set user_avatar='" + image_oss + "' where _id=" + id;
                            appDB.update("pc_user", update_sql);
                            json = AppJsonUtils.returnSuccessJsonString(result, "上传成功！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                    }
                    result.put("error_code", ErrorCode.PARAMETER_WRONG);
                    json = AppJsonUtils.returnFailJsonString(result, "上传失败，请重试！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                case "user_base":
                    if (id != 0) {
                        json = AppJsonUtils.returnSuccessJsonString(AppJsonUtils.getUserInfo(appDB, id), "用户基本信息获取成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.TOKEN_EXPIRED);
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
            }
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.PARAMETER_WRONG);
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }


}
