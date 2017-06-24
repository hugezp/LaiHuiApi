package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.ErrorCode;
import com.cyparty.laihui.utilities.AppJsonUtils;
import com.cyparty.laihui.utilities.Utils;
import com.cyparty.laihui.utilities.sdk.GeetestConfig;
import com.cyparty.laihui.utilities.sdk.GeetestLib;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by Administrator on 2017/4/8.
 */
@Controller
@RequestMapping(value = "/api/app")
public class GeetestController {

    @Autowired
    AppDB appDB;

    /**
     * geetest 滑动验证模块
     * 客户端第一次请求，从第三方取得数据返回给客户端
     * @param request
     * @return
     */

    @RequestMapping(value = "/verify/address", method = RequestMethod.GET)
    public ResponseEntity<String> address(HttpServletRequest request) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetest_id(), GeetestConfig.getGeetest_key(),
                GeetestConfig.isnewfailback());
        String resStr = "{}";
        //自定义userid
        String userid = "lhpc@2017";
        //进行验证预处理
        int gtServerStatus = gtSdk.preProcess(userid);
        //将服务器状态设置到session中
        request.getSession().setAttribute(gtSdk.gtServerStatusSessionKey, gtServerStatus);
        //将userid设置到session中
        request.getSession().setAttribute("userid", userid);
        resStr = gtSdk.getResponseStr();

        return new ResponseEntity<>(resStr, responseHeaders, HttpStatus.OK);
    }

    /**
     * 客户端第二次请求，进行第二次验证
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/verify/date", method = RequestMethod.POST)
    public ResponseEntity<String> date(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        GeetestLib gtSdk = new GeetestLib(GeetestConfig.getGeetest_id(), GeetestConfig.getGeetest_key(),
                GeetestConfig.isnewfailback());
        String challenge = request.getParameter(GeetestLib.fn_geetest_challenge);
        String validate = request.getParameter(GeetestLib.fn_geetest_validate);
        String seccode = request.getParameter(GeetestLib.fn_geetest_seccode);
        //取得手机号
        String mobile = request.getParameter("mobile");
        //从session中获取gt-server状态
        int gt_server_status_code = 2017;
        //从session中获取userid
        String userid = "lhpc@2017";
        int gtResult = 2016;

        if (gt_server_status_code == 2017) {
            //gt-server正常，向gt-server进行二次验证
            gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, userid);

        } else {
            // gt-server非正常情况下，进行failback模式验证
            System.out.println("failback:use your own server captcha validate");
            gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
            System.out.println(gtResult);
        }
        String code = "";
        String json = "";
        JSONObject result = new JSONObject();
        // 验证成功
        if (gtResult == 2017) {
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
        } else {
            // 验证失败
            json = AppJsonUtils.returnFailJsonString(result, "验证失败！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }

    }

    /**
     * 调用短信接口
     * 如果sms_status=1 聚合短信
     * 如果sms_status=2 mob短信
     *
     * @param request
     * @return
     */

    @RequestMapping(value = "/sms/status", method = RequestMethod.POST)
    public ResponseEntity<String> smsStatus(HttpServletRequest request) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        result.put("sms_status",2);
        json = AppJsonUtils.returnSuccessJsonString(result,"获取状态成功");
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }

}
