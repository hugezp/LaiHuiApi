package com.cyparty.laihui.controller.willArrive;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.utilities.PayConfigUtils;
import com.cyparty.laihui.utilities.Utils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.cyparty.laihui.controller.PayOrderController.buildOrderParam;
import static com.cyparty.laihui.controller.PayOrderController.getSign;
import static com.cyparty.laihui.controller.PayOrderController.senPost;

/**
 * Created by pangzhenpeng on 2017/6/23.
 */
@Controller
public class ArrivePayController {
    /**
     * 乘客必达单支付支付接口
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/api/app/arrive/pay/info", method = RequestMethod.POST)
    public ResponseEntity<String> pay(HttpServletRequest request, HttpServletResponse response) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        //乘客车单ID
        double price = Double.parseDouble(request.getParameter("price"));
        String tradeNo = request.getParameter("tradeNo");
        String pay_type = request.getParameter("pay_type");
        double serviceFee = 0.0;
        String body = "拼车费用";
        String description = "拼车费用";
        if (pay_type != null && !pay_type.isEmpty()) {
            //微信支付(来回公司名下)
            String now_ip = Utils.getIP(request);
            String nonce_str = Utils.getCharAndNum(32);
            double inputFee = 1;
           // double inputFee = (price + serviceFee) * 100;
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
            paraMap.put("out_trade_no", tradeNo);
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
                    "   <out_trade_no>" + tradeNo + "</out_trade_no>\n" +
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

        } else {
            //支付宝支付
            double total_fee = 0.01;
            //double total_fee = price + serviceFee;
            Map<String, String> keyValues = new HashMap<String, String>();
            String current_time = Utils.getCurrentTime();
            keyValues.put("app_id", PayConfigUtils.getApp_id());

            keyValues.put("biz_content", "{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"" + total_fee + "\",\"subject\":\"" + description + "\",\"body\":\"" + body + "\",\"out_trade_no\":\"" + tradeNo + "\"}");

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
}
