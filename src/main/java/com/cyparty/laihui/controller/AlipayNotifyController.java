package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import com.cyparty.laihui.utilities.*;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;


/**
 * Created by zhu on 2016/5/11.
 */
@Controller
public class AlipayNotifyController {
    private static final String publickey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
    @Autowired
    AppDB appDB;
    @Autowired
    NotifyPush notifyPush;
    @ResponseBody
    @RequestMapping(value = "/alipay/notify", method = RequestMethod.POST)
    public ResponseEntity<String> departure(HttpServletRequest request,HttpServletResponse response) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        boolean is_success ;
        Map<String, String[]> parameterMap = request.getParameterMap();

        int size = parameterMap.size()-2;
        String[] parameters = new String[size];
        Map<String,String> params=new HashMap<>();
        int i = 0;
        for (String key : parameterMap.keySet()) {
            if(!key.equals("sign")&&!key.equals("sign_type")){
                parameters[i] = key;
                params.put(key,parameterMap.get(key)[0]);
                i++;
            }
        }


        String result_parameter = "";
        Arrays.sort(parameters);
        for (String str : parameters) {
            result_parameter = result_parameter + str +"="+ parameterMap.get(str)[0]+"&";
        }
        result_parameter=result_parameter.substring(0,result_parameter.length()-1);

        String sign = request.getParameter("sign");

        System.out.println("-----开始处理支付宝通知------");
        is_success=verify(result_parameter,sign);
        System.out.println("verify校验："+is_success);

        String notify_time = request.getParameter("notify_time");
        String notify_type = request.getParameter("notify_type");
        String notify_id = request.getParameter("notify_id");

        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String price = request.getParameter("total_amount");
        String receipt_amount = request.getParameter("receipt_amount");
        String buyer_pay_amount = request.getParameter("buyer_pay_amount");
        String point_amount = request.getParameter("point_amount");
        String seller_id = request.getParameter("seller_id");
        String seller_email = request.getParameter("seller_email");

        String buyer_email = request.getParameter("buyer_logon_id");
        String buyer_id = request.getParameter("buyer_id");
        String trade_status = request.getParameter("trade_status");

        if(is_success){
            if(trade_status!=null){
                AlipayNotify alipay=new AlipayNotify();

                alipay.setBuyer_email(buyer_email);
                alipay.setBuyer_id(buyer_id);
                alipay.setSeller_email(seller_email);
                alipay.setSeller_id(seller_id);
                alipay.setTrade_status(trade_status);
                alipay.setPrice(price);
                alipay.setReceipt_amount(receipt_amount);
                alipay.setBuyer_pay_amount(buyer_pay_amount);
                alipay.setPoint_amount(point_amount);
                alipay.setOut_trade_no(out_trade_no);
                alipay.setTrade_no(trade_no);
                alipay.setNotify_type(notify_type);
                alipay.setNotify_time(notify_time);
                alipay.setNotify_id(notify_id);

                //todo:查询,避免同一订单多次插入
                String check_where=" where out_trade_no='"+out_trade_no+"' and (trade_status ='TRADE_SUCCESS' or trade_status ='TRADE_FINISHED')";
                List<AlipayNotify> alipayNotifyList=appDB.getAlipayNotify(check_where);
                if(alipayNotifyList.size()>0){
                    PrintWriter out= null;
                    try {
                        response.reset();
                        out = response.getWriter();
                        out.write("success");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        out.close();
                    }
                    json = AppJsonUtils.returnSuccessJsonString(result, "响应成功");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
                is_success=appDB.createPayNotify(alipay, 0);
                System.out.println("支付log创建成功！");
                boolean is_pay_success=false;
                if(trade_status.equals("TRADE_SUCCESS")||trade_status.equals("TRADE_FINISHED")){
                    is_pay_success=true;
                }
                if(is_success&&is_pay_success){

                            String where =" where trade_no="+out_trade_no;
                            PassengerOrder departureInfo;
                            List<PassengerOrder> passengerOrderList = appDB.getPassengerDepartureInfo(where);
                            if(passengerOrderList.size()>0){
                                //支付完成，更改各个状态

                                departureInfo=passengerOrderList.get(0);
                                String update_sql=" set order_status=3 ,update_time='"+Utils.getCurrentTime()+"' where order_id="+departureInfo.get_id()+" and order_type=0";//记录状态
                                appDB.update("pc_orders",update_sql);
                                update_sql=" set order_status=2 ,update_time='"+Utils.getCurrentTime()+"' where order_id="+departureInfo.get_id()+" and order_type=2 and order_status=1 ";//抢单状态
                                appDB.update("pc_orders",update_sql);
                                System.out.println("订单状态更新成功！");
                                //todo：乘客支付记录
                                int user_id=departureInfo.getUser_id();
                                int order_id=departureInfo.get_id();
                                int p_id=departureInfo.getP_id();
                                double money=departureInfo.getPay_money();
                                int driver_id=0;
                                int grab_id=0;
                                String d_mobile="";
                                where=" a left join pc_user b on a.user_id=b._id where order_id="+departureInfo.get_id()+" and order_type=2 and order_status=2";
                                List<Order> orderList=appDB.getOrderReview(where,1);
                                if(orderList.size()>0){
                                    driver_id=orderList.get(0).getUser_id();
                                    grab_id=orderList.get(0).get_id();
                                    d_mobile=orderList.get(0).getUser_mobile();
                                }
                                //支付成功，座位锁定，数据库中剩余座位减
                                update_sql=" set current_seats = current_seats-"+departureInfo.getSeats()+" where user_id= "+driver_id+" and departure_time='"+Utils.getCurrentTime()+"' and is_enable=1";
                                appDB.update("pc_driver_publish_info",update_sql);
                                where = " a left join pc_user b on a.user_id=b._id where order_id=" + departureInfo.get_id() + " and order_type=0";
                                List<Order> passengerList = appDB.getOrderReview(where, 1);
                                String p_name="";
                                if (passengerList.size() > 0) {
                                    p_name = passengerList.get(0).getUser_name();
                                }
                                PayLog pay=new PayLog();
                                pay.setUser_id(user_id);
                                pay.setOrder_id(order_id);
                                pay.setP_id(p_id);
                                pay.setCash(money);
                                pay.setDriver_id(driver_id);
                                pay.setAction_type(0);
                                pay.setPay_type(0);
                                pay.setOrder_status(1);
                                pay.setDeparture_time(departureInfo.getDeparture_time());

                                appDB.createPayLog(pay);

                                //推送通知车主
                                String title="车主";
                                String time=Utils.getCurrentTime();
                                String content="乘客"+p_name+"在"+time.substring(0,time.length()-3)+"完成了支付，";
                                Utils.sendAllNotifyMessage(d_mobile,title,content);
                               content="乘客"+p_name+"在"+time.substring(0,time.length()-3)+"通过支付宝完成了支付，祝您路途愉快！";
                                JSONObject jsonObject=new JSONObject();
                                jsonObject.put("order_status",100);
                                //保存到消息数据库中
                                int push_id = user_id;
                                int receive_id = driver_id;
                                int push_type = 26;
                                boolean is_true = appDB.createPush(grab_id,push_id,receive_id,push_type,content,11,"11.caf",jsonObject.toJSONString(),1,p_name,null);

                                notifyPush.pinCheNotify("26",d_mobile,content,grab_id,jsonObject,Utils.getCurrentTime());
                                System.out.println("支付记录保存成功！");

                            }else {
                                System.out.println("未查询到该商户号对应的订单信息");
                            }
                            PrintWriter out = null;
                            try {
                                response.reset();
                                out = response.getWriter();
                                out.write("success");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                out.close();
                            }
                }
            }
        }
        json = AppJsonUtils.returnSuccessJsonString(result, "响应成功");
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }
    //校验sign
    public static boolean verify( String plainText, String sign) {
        try {
            boolean is_passed= AlipaySignature.rsaCheckContent(plainText, sign, publickey, "utf-8");

            // 验证签名是否正常
            return is_passed;
        } catch (Throwable e) {
            System.out.println("校验签名失败");
            e.printStackTrace();
            return false;
        }
    }

    @RequestMapping(value = "/wx_pay/notify", method = RequestMethod.POST)
    public ResponseEntity<String> wx_pay(HttpServletRequest request,HttpServletResponse response) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        String json = "";
        try {
            PrintWriter out=response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String response_content="<xml> \n" +
                "\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml> \n";

        //获取回执参数
        System.out.println("-----开始处理微信通知------");
        InputStream is;
        String return_xml=null;
        try {
            is = request.getInputStream();
            return_xml= IOUtils.toString(is, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("微信异步通知内容："+return_xml);
        boolean is_success = true;
        Document doc ;
        Map<String, String> parameterMap = new HashMap<>();
        try {
            doc = DocumentHelper.parseText(return_xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            Iterator return_code = rootElt.elementIterator("return_code"); // 获取根节点下的子节点return_code
            while (return_code.hasNext()) {
                Element recordEle = (Element) return_code.next();
                String code = recordEle.getText(); // 拿到return_code返回值
                if(code!=null&&code.equals("SUCCESS"))
                {
                    is_success=true;
                }
                System.out.println("code:" + code);
            }
            if(is_success){
                //System.out.println("得到的xml:"+return_xml);
                parameterMap= XmlParse.parse(return_xml);
            }else {
                //直接停止执行
                PrintWriter out = null;
                try {
                    response.reset();
                    out = response.getWriter();
                    out.write(response_content);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    out.close();
                }
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String sign = parameterMap.get("sign");
        String result_code = parameterMap.get("result_code");
        String out_trade_no = parameterMap.get("out_trade_no");
        //System.out.println("map大小："+parameterMap.size()+"sign="+sign+"result_code="+result_code+"out_trade_no="+out_trade_no);

        List<String> keys=new ArrayList<>(parameterMap.keySet());
        keys.remove("sign");
        String result_parameter = "";
        Collections.sort(keys);
        for (String str : keys) {
            result_parameter = result_parameter + str +"="+ parameterMap.get(str)+"&";
        }
        result_parameter=result_parameter+"key="+ PayConfigUtils.getWx_app_secret_key();

        //System.out.println("待签名字符串为："+result_parameter);
        String current_sign=Utils.encode("MD5",result_parameter).toUpperCase();
        if(current_sign.equals(sign))
        {
            is_success=true;
        }else {
            is_success=false;
        }
        System.out.println("微信支付签名校验："+is_success);

        if(is_success){
            if(result_code.equals("SUCCESS")){
                //System.out.println("查询是否已经收到异步通知！");
                String check_where=" where out_trade_no='"+out_trade_no+"' and trade_status ='SUCCESS'";
                List<AlipayNotify> alipayNotifyList=appDB.getAlipayNotify(check_where);
                if(alipayNotifyList.size()>0){
                    PrintWriter out= null;
                    try {
                        response.reset();
                        out = response.getWriter();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out.write(response_content);
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
                System.out.println("开始创建支付log！");
                //Todo:创建微信支付记录
                AlipayNotify wxPay=new AlipayNotify();
                double price=0;
                double pay_amount=0;
                try {
                    price=Integer.parseInt(parameterMap.get("total_fee"))/100d;
                    pay_amount=Integer.parseInt(parameterMap.get("cash_fee"))/100d;
                } catch (NumberFormatException e) {
                    price=0;
                    pay_amount=0;
                    e.printStackTrace();
                }
                wxPay.setPrice(price+"");
                wxPay.setBuyer_pay_amount(pay_amount+"");
                wxPay.setTrade_no(parameterMap.get("transaction_id"));
                wxPay.setOut_trade_no(parameterMap.get("out_trade_no"));
                wxPay.setTrade_status(parameterMap.get("result_code"));
                wxPay.setBuyer_id(parameterMap.get("openid"));
                wxPay.setSeller_id(parameterMap.get("mch_id"));

                is_success=appDB.createPayNotify(wxPay, 1);
                System.out.println("微信支付log创建成功！");
                if(is_success) {
                    String where = " where trade_no=" + out_trade_no;
                    PassengerOrder departureInfo;
                    List<PassengerOrder> passengerOrderList = appDB.getPassengerDepartureInfo(where);
                    if (passengerOrderList.size() > 0) {
                        //支付完成，更改各个状态
                        departureInfo = passengerOrderList.get(0);

                        String update_sql = " set order_status=3 ,update_time='" + Utils.getCurrentTime() + "' where order_id=" + departureInfo.get_id() + " and order_type=0";//记录状态
                        appDB.update("pc_orders", update_sql);

                        update_sql = " set order_status=2 ,update_time='" + Utils.getCurrentTime() + "' where order_id=" + departureInfo.get_id() + " and order_type=2 and order_status=1 ";//抢单状态
                        appDB.update("pc_orders", update_sql);
                        System.out.println("订单状态更新成功！");
                        //用户支付记录
                        int user_id = departureInfo.getUser_id();
                        int order_id = departureInfo.get_id();
                        int p_id = departureInfo.getP_id();
                        double money = departureInfo.getPay_money();
                        int driver_id = 0;
                        int grab_id = 0;
                        String d_mobile="";
                        where = " a left join pc_user b on a.user_id=b._id where order_id=" + departureInfo.get_id() + " and order_type=2 and order_status=2";
                        List<Order> orderList = appDB.getOrderReview(where, 1);
                        if (orderList.size() > 0) {
                            grab_id=orderList.get(0).get_id();
                            driver_id = orderList.get(0).getUser_id();
                            d_mobile=orderList.get(0).getUser_mobile();
                        }
                        //支付成功，座位锁定，数据库中剩余座位减相应座位数
                        update_sql=" set current_seats = current_seats-"+departureInfo.getSeats()+" where user_id= "+driver_id+" and departure_time='"+Utils.getCurrentTime()+"' and is_enable=1";
                        appDB.update("pc_driver_publish_info",update_sql);

                        where = " a left join pc_user b on a.user_id=b._id where order_id=" + departureInfo.get_id() + " and order_type=0";
                        List<Order> passengerList = appDB.getOrderReview(where, 1);
                        String p_name="";
                        if (passengerList.size() > 0) {
                            p_name = passengerList.get(0).getUser_name();
                        }
                        PayLog pay = new PayLog();
                        pay.setUser_id(user_id);
                        pay.setOrder_id(order_id);
                        pay.setP_id(p_id);
                        pay.setCash(money);
                        pay.setDriver_id(driver_id);
                        pay.setAction_type(0);
                        pay.setPay_type(1);
                        pay.setOrder_status(1);
                        pay.setDeparture_time(departureInfo.getDeparture_time());

                        appDB.createPayLog(pay);

                        //推送通知车主
                        String title="车主";
                        String time=Utils.getCurrentTime();
                        String content="乘客"+p_name+"在"+time.substring(0,time.length()-3)+"完成了支付，";
                        Utils.sendAllNotifyMessage(d_mobile,title,content);


                        content="乘客"+p_name+"在"+time.substring(0,time.length()-3)+"通过微信完成了支付，祝您路途愉快！";
                        JSONObject jsonObject=new JSONObject();
                        jsonObject.put("order_status",100);
                        //保存到消息数据库中
                        int push_id = user_id;
                        int receive_id = driver_id;
                        int push_type = 26;
                        boolean is_true = appDB.createPush(grab_id,push_id,receive_id,push_type,content,11,"11.caf",jsonObject.toJSONString(),1,p_name,null);

                        notifyPush.pinCheNotify("26",d_mobile,content,grab_id,jsonObject,Utils.getCurrentTime());
                        System.out.println("微信支付记录保存成功！");
                    } else {
                        System.out.println("未查询到该商户号对应的订单信息");
                    }
                    PrintWriter out = null;
                    try {
                        response.reset();
                        out = response.getWriter();
                        out.write(response_content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        out.close();
                    }
                }
            }
        }

        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }

}
