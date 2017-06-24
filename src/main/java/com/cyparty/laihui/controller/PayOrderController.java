package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;

import com.cyparty.laihui.db.AppDB;

import com.cyparty.laihui.domain.PassengerOrder;
import com.cyparty.laihui.utilities.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static com.alipay.api.internal.util.AlipaySignature.getSignContent;


/**
 * Created by zhu on 2016/5/11.
 */
@Controller
public class PayOrderController {

    @Autowired
    AppDB appDB;

    /**
     * 支付接口
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/api/app/pay/info", method = RequestMethod.POST)
    public ResponseEntity<String> pay(HttpServletRequest request, HttpServletResponse response) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        //乘客车单ID
        String order_id = request.getParameter("order_id");
        String pay_type = request.getParameter("pay_type");
        String flag = request.getParameter("flag");
        String body = "拼车费用";
        String description = "拼车费用";
        PassengerOrder passengerOrder;
        if (order_id != null && !order_id.isEmpty()) {
            String where = " where a._id=" + order_id + "  and a.is_enable=1 ";
            List<PassengerOrder> passengerOrderList = appDB.getPassengerDepartureInfo(where);
            if (passengerOrderList.size() > 0) {
                passengerOrder = passengerOrderList.get(0);
                if (passengerOrder.getIsArrive()==1){
                }
            } else {
                json = AppJsonUtils.returnFailJsonString(result, "订单已失效！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
        } else {
            json = AppJsonUtils.returnFailJsonString(result, "参数错误！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }

        if (pay_type != null && !pay_type.isEmpty()) {
            //微信支付(来回公司名下)
            if (flag != null && !flag.isEmpty()) {
                String now_ip = Utils.getIP(request);
                String nonce_str = Utils.getCharAndNum(32);
                double inputFee = 0.0;
                inputFee = passengerOrder.getPay_money() * 100;
                int inputIntFee = (int) inputFee;
                String total_fee = inputIntFee + "";

                String prepay_id = null;
                Map<String, String> paraMap = new HashMap<>();
                paraMap.put("appid", PayConfigUtils.getWx_laihui_app_id());
                paraMap.put("attach", description);
                paraMap.put("body", body);
                paraMap.put("mch_id", PayConfigUtils.getWx_laihui_mch_id());
                paraMap.put("nonce_str", nonce_str);
                paraMap.put("notify_url", PayConfigUtils.getWx_pay_laihui_notify_url());
                paraMap.put("out_trade_no", passengerOrder.getPay_num());
                paraMap.put("spbill_create_ip", now_ip);
                paraMap.put("total_fee", total_fee);
                paraMap.put("trade_type", "APP");
                List<String> keys = new ArrayList<>(paraMap.keySet());
                Collections.sort(keys);

                StringBuilder authInfo = new StringBuilder();
                for (int i = 0; i < keys.size() - 1; i++) {
                    String value = paraMap.get(keys.get(i));
                    authInfo.append(keys.get(i) + "=" + value + "&");
                }
                authInfo.append(keys.get(keys.size() - 1) + "=" + paraMap.get(keys.get(keys.size() - 1)));
                String stringA = authInfo.toString() + "&key=" + PayConfigUtils.getWx_laihui_app_secret_key();
                String sign = Utils.encode("MD5", stringA).toUpperCase();
                //封装xml
                String paras = "<xml>\n" +
                        "   <appid>" + PayConfigUtils.getWx_laihui_app_id() + "</appid>\n" +
                        "   <attach>" + description + "</attach>\n" +
                        "   <body>" + body + "</body>\n" +
                        "   <mch_id>" + PayConfigUtils.getWx_laihui_mch_id() + "</mch_id>\n" +
                        "   <nonce_str>" + nonce_str + "</nonce_str>\n" +
                        "   <notify_url>" + PayConfigUtils.getWx_pay_laihui_notify_url() + "</notify_url>\n" +
                        "   <out_trade_no>" + passengerOrder.getPay_num() + "</out_trade_no>\n" +
                        "   <spbill_create_ip>" + now_ip + "</spbill_create_ip>\n" +
                        "   <total_fee>" + total_fee + "</total_fee>\n" +
                        "   <trade_type>APP</trade_type>\n" +
                        "   <sign>" + sign + "</sign>\n" +
                        "</xml>";
                try {
                    String content = senPost(paras);
                    if (content != null) {
                        prepay_id = Utils.readStringXml(content);
                    }
                    if (prepay_id != null) {
                        String current_noncestr = Utils.getCharAndNum(32);
                        String current_sign = null;
                        long current_timestamp = System.currentTimeMillis() / 1000;
                        result.put("appid", PayConfigUtils.getWx_laihui_app_id());
                        result.put("partnerid", PayConfigUtils.getWx_laihui_mch_id());
                        result.put("prepayid", prepay_id);
                        result.put("package", "Sign=WXPay");
                        result.put("noncestr", current_noncestr);
                        result.put("timestamp", current_timestamp);
                        //加密算法
                        String nowStringA = "appid=" + PayConfigUtils.getWx_laihui_app_id() + "&noncestr=" + current_noncestr + "&package=Sign=WXPay&partnerid=" + PayConfigUtils.getWx_laihui_mch_id() + "&prepayid=" + prepay_id + "&timestamp=" + current_timestamp + "&key=" + PayConfigUtils.getWx_laihui_app_secret_key();
                        current_sign = Utils.encode("MD5", nowStringA).toUpperCase();
                        result.put("sign", current_sign);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new ResponseEntity<>(result.toString(), responseHeaders, HttpStatus.OK);
                //微信支付(锦程百贷名下)
            } else {
                String now_ip = Utils.getIP(request);
                String nonce_str = Utils.getCharAndNum(32);
                double inputFee = passengerOrder.getPay_money() * 100;
                int inputIntFee = (int) inputFee;
                String total_fee = inputIntFee + "";
                String prepay_id = null;
                Map<String, String> paraMap = new HashMap<>();
                paraMap.put("appid", PayConfigUtils.getWx_app_id());
                paraMap.put("attach", description);
                paraMap.put("body", body);
                paraMap.put("mch_id", PayConfigUtils.getWx_mch_id());
                paraMap.put("nonce_str", nonce_str);
                paraMap.put("notify_url", PayConfigUtils.getWx_pay_notify_url());
                paraMap.put("out_trade_no", passengerOrder.getPay_num());
                paraMap.put("spbill_create_ip", now_ip);
                paraMap.put("total_fee", total_fee);
                paraMap.put("trade_type", "APP");
                List<String> keys = new ArrayList<>(paraMap.keySet());
                Collections.sort(keys);

                StringBuilder authInfo = new StringBuilder();
                for (int i = 0; i < keys.size() - 1; i++) {
                    String value = paraMap.get(keys.get(i));
                    authInfo.append(keys.get(i) + "=" + value + "&");
                }
                authInfo.append(keys.get(keys.size() - 1) + "=" + paraMap.get(keys.get(keys.size() - 1)));
                String stringA = authInfo.toString() + "&key=" + PayConfigUtils.getWx_app_secret_key();
                String sign = Utils.encode("MD5", stringA).toUpperCase();
                //封装xml
                String paras = "<xml>\n" +
                        "   <appid>" + PayConfigUtils.getWx_app_id() + "</appid>\n" +
                        "   <attach>" + description + "</attach>\n" +
                        "   <body>" + body + "</body>\n" +
                        "   <mch_id>" + PayConfigUtils.getWx_mch_id() + "</mch_id>\n" +
                        "   <nonce_str>" + nonce_str + "</nonce_str>\n" +
                        "   <notify_url>" + PayConfigUtils.getWx_pay_notify_url() + "</notify_url>\n" +
                        "   <out_trade_no>" + passengerOrder.getPay_num() + "</out_trade_no>\n" +
                        "   <spbill_create_ip>" + now_ip + "</spbill_create_ip>\n" +
                        "   <total_fee>" + total_fee + "</total_fee>\n" +
                        "   <trade_type>APP</trade_type>\n" +
                        "   <sign>" + sign + "</sign>\n" +
                        "</xml>";
                try {
                    String content = senPost(paras);
                    if (content != null) {
                        prepay_id = Utils.readStringXml(content);
                    }
                    if (prepay_id != null) {
                        String current_noncestr = Utils.getCharAndNum(32);
                        String current_sign = null;
                        long current_timestamp = System.currentTimeMillis() / 1000;
                        result.put("appid", PayConfigUtils.getWx_app_id());
                        result.put("partnerid", PayConfigUtils.getWx_mch_id());
                        result.put("prepayid", prepay_id);
                        result.put("package", "Sign=WXPay");
                        result.put("noncestr", current_noncestr);
                        result.put("timestamp", current_timestamp);
                        //加密算法
                        String nowStringA = "appid=" + PayConfigUtils.getWx_app_id() + "&noncestr=" + current_noncestr + "&package=Sign=WXPay&partnerid=" + PayConfigUtils.getWx_mch_id() + "&prepayid=" + prepay_id + "&timestamp=" + current_timestamp + "&key=" + PayConfigUtils.getWx_app_secret_key();
                        current_sign = Utils.encode("MD5", nowStringA).toUpperCase();
                        result.put("sign", current_sign);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new ResponseEntity<>(result.toString(), responseHeaders, HttpStatus.OK);
            }

        } else {
            //支付宝支付
            double total_fee = passengerOrder.getPay_money();
            Map<String, String> keyValues = new HashMap<String, String>();
            String current_time = Utils.getCurrentTime();
            keyValues.put("app_id", PayConfigUtils.getApp_id());

            keyValues.put("biz_content", "{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"" + total_fee + "\",\"subject\":\"" + description + "\",\"body\":\"" + body + "\",\"out_trade_no\":\"" + passengerOrder.getPay_num() + "\"}");

            keyValues.put("charset", "utf-8");

            keyValues.put("method", "alipay.trade.app.pay");

            keyValues.put("sign_type", "RSA");

            keyValues.put("timestamp", current_time);

            keyValues.put("version", "1.0");

            keyValues.put("notify_url", PayConfigUtils.getAlipay_notify_url());
            String sign = getSign(keyValues, PayConfigUtils.getPrivate_key());
            json = buildOrderParam(keyValues) + "&" + sign;
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }

    }

    /**
     * 微信公众号支付接口
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/api/app/wx_pay", method = RequestMethod.POST)
    public ResponseEntity<String> WxPaySendData(HttpServletRequest request, HttpServletResponse response) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json = "";
        //乘客车单ID
        String order_id = request.getParameter("order_id");
        String pay_type = request.getParameter("pay_type");
        String code = request.getParameter("code");
        String openId = "";
        if (null != code) {
            openId = PayConfigUtils.getOpenId(code);
        }
        String body = "拼车费用";
        String description = "拼车费用";
        PassengerOrder passengerOrder;
        if (order_id != null && !order_id.isEmpty()) {
            String where = " where a._id=" + order_id + "  and a.is_enable=1 ";
            List<PassengerOrder> passengerOrderList = appDB.getPassengerDepartureInfo(where);
            if (passengerOrderList.size() > 0) {
                passengerOrder = passengerOrderList.get(0);
            } else {
                json = AppJsonUtils.returnFailJsonString(result, "订单已失效！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
        } else {
            json = AppJsonUtils.returnFailJsonString(result, "参数错误！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
        if (pay_type != null && !pay_type.isEmpty()) {
            String now_ip = Utils.getIP(request);
            String nonce_str = Utils.getCharAndNum(32);
            double inputFee = 0.1;
            inputFee = passengerOrder.getPay_money() * 100;
            int inputIntFee = (int) inputFee;
            String total_fee = inputIntFee + "";
            String prepay_id = null;
            Map<String, String> paraMap = new HashMap<>();
            paraMap.put("appid", PayConfigUtils.getWx_web_app_id());
            paraMap.put("attach", description);
            paraMap.put("body", body);
            paraMap.put("mch_id", PayConfigUtils.getWx_web_mch_id());
            paraMap.put("nonce_str", nonce_str);
            paraMap.put("openid", openId);
            paraMap.put("out_trade_no", passengerOrder.getPay_num());
            paraMap.put("spbill_create_ip", now_ip);
            paraMap.put("total_fee", total_fee);
            paraMap.put("trade_type", "JSAPI");
            paraMap.put("notify_url", PayConfigUtils.getWx_pay_web_notify_url());
            List<String> keys = new ArrayList<>(paraMap.keySet());
            Collections.sort(keys);
            StringBuilder authInfo = new StringBuilder();
            for (int i = 0; i < keys.size() - 1; i++) {
                String value = paraMap.get(keys.get(i));
                authInfo.append(keys.get(i) + "=" + value + "&");
            }
            authInfo.append(keys.get(keys.size() - 1) + "=" + paraMap.get(keys.get(keys.size() - 1)));
            String stringA = authInfo.toString() + "&key=" + PayConfigUtils.getWx_web_mch_secret_key();
            System.out.println(stringA);
            String sign = Utils.encode("MD5", stringA).toUpperCase();
            String trade_type = "JSAPI";
            //封装xml
            String paras = "<xml>\n" +
                    "<appid>" + PayConfigUtils.getWx_web_app_id() + "</appid>\n" +
                    "<mch_id>" + PayConfigUtils.getWx_web_mch_id() + "</mch_id>\n" +
                    "<nonce_str>" + nonce_str + "</nonce_str>\n" +
                    "<sign>" + sign + "</sign>\n" +
                    "<body><![CDATA[" + body + "]]></body>\n" +
                    "<attach>" + description + "</attach>\n" +
                    "<out_trade_no>" + passengerOrder.getPay_num() + "</out_trade_no>\n" +
                    "<total_fee>" + total_fee + "</total_fee>\n" +
                    "<spbill_create_ip>" + now_ip + "</spbill_create_ip>\n" +
                    "<notify_url>" + PayConfigUtils.getWx_pay_web_notify_url() + "</notify_url>\n" +
                    "<trade_type>" + trade_type + "</trade_type>\n" +
                    "<openid>" + openId + "</openid>\n" +
                    "</xml>";
            try {
                String content = senPost(paras);
                if (content != null) {
                    prepay_id = Utils.readStringXml(content);
                }
                if (prepay_id != null) {
                    String current_noncestr = Utils.getCharAndNum(32);
                    String current_sign = null;
                    long current_timestamp = System.currentTimeMillis() / 1000;
                    JSONObject signn = new JSONObject();
                    signn.put("appid", PayConfigUtils.getWx_web_app_id());
                    signn.put("partnerid", PayConfigUtils.getWx_web_mch_id());
                    signn.put("prepayid", prepay_id);
                    signn.put("package", "Sign=WXPay");
                    signn.put("noncestr", current_noncestr);
                    signn.put("timestamp", current_timestamp);
                    //加密算法
                    String nowStringA = "appid=" + PayConfigUtils.getWx_web_app_id() + "&noncestr=" + current_noncestr + "&package=Sign=WXPay&partnerid=" + PayConfigUtils.getWx_web_mch_id() + "&prepayid=" + prepay_id + "&timestamp=" + current_timestamp + "&key=" + PayConfigUtils.getWx_web_app_secret_key();
                    current_sign = Utils.encode("MD5", nowStringA).toUpperCase();
                    signn.put("sign", current_sign);

                    SortedMap<String, String> finalpackage = new TreeMap<String, String>();
                    String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
                    String packages = "prepay_id=" + prepay_id;
                    ;//订单详情扩展字符串
                    finalpackage.put("appId", PayConfigUtils.getWx_web_app_id());//公众号appid
                    finalpackage.put("timeStamp", timeStamp);
                    finalpackage.put("nonceStr", current_noncestr); //随机数
                    finalpackage.put("package", packages);
                    finalpackage.put("signType", "MD5");//签名方式
                    StringBuilder finals = new StringBuilder();
                    List<String> likes = new ArrayList<>(finalpackage.keySet());
                    Collections.sort(likes);
                    for (int i = 0; i < likes.size() - 1; i++) {
                        String value = finalpackage.get(likes.get(i));
                        finals.append(likes.get(i) + "=" + value + "&");
                    }
                    finals.append(likes.get(likes.size() - 1) + "=" + finalpackage.get(likes.get(likes.size() - 1)));
                    String stringB = finals.toString() + "&key=" + PayConfigUtils.getWx_web_mch_secret_key();
                    System.out.println(stringB);
                    String finalsign = Utils.encode("MD5", stringB).toUpperCase();
                    result.put("appId", PayConfigUtils.getWx_web_app_id());
                    result.put("timeStamp", current_timestamp);
                    result.put("nonceStr", current_noncestr);
                    result.put("packages", packages);
                    result.put("paySign", finalsign);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return new ResponseEntity<>(result.toString(), responseHeaders, HttpStatus.OK);
        }
        return new ResponseEntity<>(result.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @RequestMapping(value = "/pay/result", method = RequestMethod.POST)
    public ResponseEntity<String> pay_result(HttpServletRequest request, HttpServletResponse response) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

        InputStream is = null;
        String contentStr = "";
        try {
            is = request.getInputStream();
            contentStr = IOUtils.toString(is, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(contentStr);
        JSONObject result = new JSONObject();
        String json = "";

        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }

    //获得签名
    public static String getSign(Map<String, String> map, String rsaKey) {
        List<String> keys = new ArrayList<String>(map.keySet());
        // key排序
        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            authInfo.append(buildKeyValue(key, value, false));
            authInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        authInfo.append(buildKeyValue(tailKey, tailValue, false));

        String oriSign = SignUtils.sign(authInfo.toString(), rsaKey);
        String encodedSign = "";

        try {
            encodedSign = URLEncoder.encode(oriSign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "sign=" + encodedSign;
    }

    /**
     * 拼接键值对
     *
     * @param key
     * @param value
     * @param isEncode
     * @return
     */
    private static String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * 构造支付订单参数信息
     *
     * @param map 支付订单参数
     * @return
     */
    public static String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<String>(map.keySet());

        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            authInfo.append(buildKeyValue(key, value, false));
            authInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        authInfo.append(buildKeyValue(tailKey, tailValue, true));

        return authInfo.toString();
    }

    public static String senPost(String paras) throws IOException {
        boolean is_success = true;
        int i = 0;
        String result = "";
        while (is_success) {

            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            StringEntity postingString = new StringEntity(paras, "UTF-8");// xml传递
            post.setEntity(postingString);
            post.setHeader("Content-type", "text/html; charset=UTF-8");
            HttpResponse response = httpClient.execute(post);
            result = EntityUtils.toString(response.getEntity());

            if (result == null || result.isEmpty()) {
                i++;
            } else {
                break;
            }
            if (i > 2) {
                break;
            }
        }

        return result;
    }


    @RequestMapping("/")
    public String index(Model model) {
        model.asMap().clear();
        return "redirect:/api/app/apk/update";
    }

    @RequestMapping("/pic")
    public String test(Model model) {
        model.asMap().clear();
        return "pic";
    }

    @ResponseBody
    @RequestMapping("/check/pic")
    public ResponseEntity<String> check_code(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        String json = null;
        JSONObject result = new JSONObject();
        String code = request.getParameter("code");
        String really_code = (String) request.getSession().getAttribute("random");
        if (really_code != null && code != null) {
            if (really_code.equals(code)) {
                result.put("status", true);
                result.put("msg", "验证码校验通过");
            } else {
                result.put("status", false);
                result.put("msg", "验证码校验失败");
            }
        } else {
            result.put("status", false);
            result.put("msg", "得到验证码为空");
        }
        json = result.toJSONString();
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }
}
