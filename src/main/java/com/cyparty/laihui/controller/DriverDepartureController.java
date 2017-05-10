package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import com.cyparty.laihui.utilities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhu on 2016/5/11.
 */
@Controller
@ResponseBody
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class DriverDepartureController {
    @Autowired
    AppDB appDB;
    @Autowired
    OssUtil ossUtil;
    @Autowired
    NotifyPush notifyPush;


    /**
     * 计算车主价格模块
     *
     * @param request
     * @return
     */
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/driver/distance/price", produces = "application/json; charset=utf-8")
    public String getPrice(HttpServletRequest request) {
        String origin_location = request.getParameter("origin_location");
        String destination_location = request.getParameter("destination_location");
        int person = 1;
        String result = "";
        JSONObject resultObject = new JSONObject();
        URL file_url = null;
        try {
            String json_url = "http://restapi.amap.com/v3/distance?key=5f128c6b72fb65b81348ca1477f3c3ce&origins=" + origin_location + "&destination=" + destination_location + "&type=1";
            file_url = new URL(json_url);
            InputStream content = (InputStream) file_url.getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(content, "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result = result + line;
            }
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
        JSONObject dataObject = JSONObject.parseObject(result);
        JSONArray dataArray = dataObject.getJSONArray("results");
        if (dataArray.size() > 0) {
            JSONObject nowObject = dataArray.getJSONObject(0);
            int distance = nowObject.getIntValue("distance");
            int duration = nowObject.getIntValue("duration");
            //正式
            double price = 0.0;
            if (distance<=200000)
                price = distance * 3.5 / 10000f;
            else
            price = distance * 3.3 / 10000f;
            //测试
//            double  price =0.01;
            DecimalFormat df = new DecimalFormat("######0.00");
            double average = price * 1000f / distance;
//            resultObject.put("price",0.01);
//            resultObject.put("total_price",0.01);
            resultObject.put("price", new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            resultObject.put("total_price", new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            resultObject.put("cost_time", duration / 60 + "分钟");
            resultObject.put("distance", distance / 1000);
            resultObject.put("average", new BigDecimal(df.format(average)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        }
        return resultObject.toString();

    }

    /***
     * 司机发车模块（创建发车单，首页列表，发车单状态修改，删除）
     * @param request
     * @return
     */
    //hello
    @ResponseBody
    @RequestMapping(value = "/driver/departure", method = RequestMethod.POST)
    public ResponseEntity<String> departure(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            String action = request.getParameter("action");
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
            String where = "";
            boolean is_success = true;
            int user_id = 0;

            String token = request.getParameter("token");
            String start_time = request.getParameter("departure_time");
            String boarding_point = request.getParameter("boarding_point");
            String breakout_point = request.getParameter("breakout_point");

            int departure_city_code = 0;
            int destination_city_code = 0;
            int departure_address_code = 0;
            int destination_address_code = 0;
            switch (action) {
                //车主发布车单
                case "add":
                    double price = 0.0;
                    try {
                        price = Double.parseDouble(request.getParameter("price"));
                    }catch (Exception e){

                    }

                    if (token != null && token.length() == 32) {
                        user_id = appDB.getIDByToken(token);
                    } else {
                        result.put("error_code", ErrorCode.getToken_expired());
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    if (user_id > 0) {
                        //判断车主是否有未完成的车单
                        String order_where = " where a.is_enable =1 and a.user_id=" + user_id;
                        if (appDB.getAppDriverDpartureInfo(order_where).size() > 0) {
                            json = AppJsonUtils.returnFailJsonString(result, "太贪心了，您尚有未完成的行程！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                        String user_where = " where _id=" + user_id;
                        String mobile = appDB.getUserList(user_where).get(0).getUser_mobile();
                        if (appDB.getUserList(user_where).get(0).getIs_validated() != 1) {
                            result.put("error_code", ErrorCode.getIs_validated());
                            json = AppJsonUtils.returnFailJsonString(result, "请先进行实名认证！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                        if (appDB.getUserList(user_where).get(0).getIs_car_owner() != 1) {
                            result.put("error_code", ErrorCode.getIs_car_owner());
                            json = AppJsonUtils.returnFailJsonString(result, "请先进行车主认证！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                        //校验次数是否超过上限
                        String current_time = Utils.getCurrentTime().split(" ")[0] + " 00:00:00";
                        String get_ticket_sql = " where user_id=" + user_id + " and create_time >='" + current_time + "'";
                        int count = appDB.getCount("pc_driver_publish_info", get_ticket_sql);
                        if (count >= ConfigUtils.getDriver_departure_counts()) {
                            result.put("current", count);
                            result.put("total", ConfigUtils.getDriver_departure_counts());
                            result.put("left", ConfigUtils.getDriver_departure_counts() - count);
                            json = AppJsonUtils.returnFailJsonString(result, "每日发布行程次数为" + ConfigUtils.getDriver_departure_counts() + "次，您今日发布次数已达到上限！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                        DepartureInfo departure = new DepartureInfo();
                        int init_seats = Integer.parseInt(request.getParameter("init_seats"));

                        String now_source = request.getParameter("source");
                        int source = 0;
                        if (now_source != null && !now_source.isEmpty() && now_source.equals("iOS")) {
                            source = 1;
                        }
                        String remark = "司机没有限制要求";
                        if (request.getParameter("remark") != null && !request.getParameter("remark").isEmpty()) {
                            remark = request.getParameter("remark");
                        }
                        try {
                            JSONObject boardingObject = JSONObject.parseObject(boarding_point);
                            departure_address_code = boardingObject.getIntValue("adCode");
                            departure_city_code = Integer.parseInt((departure_address_code + "").substring(0, 4) + "00");
                            JSONObject breakoutObject = JSONObject.parseObject(breakout_point);
                            destination_address_code = breakoutObject.getIntValue("adCode");
                            destination_city_code = Integer.parseInt((destination_address_code + "").substring(0, 4) + "00");
                        } catch (Exception e) {
                            e.printStackTrace();
                            result.put("error_code", ErrorCode.getParameter_wrong());
                            json = AppJsonUtils.returnFailJsonString(result, "发布失败！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                        departure.setUser_id(user_id);
                        departure.setMobile(mobile);
                        departure.setStart_time(start_time);
                        departure.setBoarding_point(boarding_point);
                        departure.setBreakout_point(breakout_point);
                        departure.setInit_seats(init_seats);
                        departure.setCurrent_seats(init_seats);
                        departure.setDeparture_city_code(departure_city_code);
                        departure.setDeparture_address_code(departure_address_code);
                        departure.setDestination_city_code(destination_city_code);
                        departure.setDestination_address_code(destination_address_code);
                        departure.setPrice(price);
                        departure.setRemark(remark);
                        //创建出车信息
                        id = appDB.createPCHDeparture(departure, source);
                        if (id > 0) {
                            //发送通知
                         /*   //SendSMSUtil.sendSMSToDriver(mobile);
                            result.put("order_id", id);
                            result.put("departure_time", start_time);
                            result.put("boarding_point", boarding_point);
                            result.put("breakout_point", breakout_point);
                            result.put("init_seats", init_seats);*/
                            //车主车单id
                            int car_id = appDB.getMaxID("_id", "pc_driver_publish_info");
                            result.put("car_id", car_id);
                            result.put("boarding_point", boarding_point);
                            result.put("breakout_point", breakout_point);
                            result.put("departure_time", start_time);
                            result.put("init_seats", init_seats);
                            result.put("current_seats", init_seats);
                            result.put("price", price);
                            json = AppJsonUtils.returnSuccessJsonString(result, "发布成功！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            result.put("error_code", ErrorCode.getParameter_wrong());
                            json = AppJsonUtils.returnFailJsonString(result, "发布失败！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                    } else {
                        result.put("error_code", ErrorCode.getToken_expired());
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                case "delete":
                    if (token != null && token.length() == 32) {
                        user_id = appDB.getIDByToken(token);
                    } else {
                        result.put("error_code", ErrorCode.getToken_expired());
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    if (user_id > 0) {
                        id = Integer.parseInt(request.getParameter("order_id"));
                        where = " where user_id=" + user_id + " and is_enable=1 and order_type=2 and order_status<3";

                        if (appDB.getOrderReview(where, 0).size() > 0) {
                            json = AppJsonUtils.returnFailJsonString(result, "还有订单未处理，请处理订单后在删除！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            where = " where is_enable=1 and a._id=" + id + " and user_id=" + user_id;
                            //验证是否是本人创建的发车单
                            List<DepartureInfo> departureInfoList = appDB.getAppDriverDpartureInfo(where);
                            if (departureInfoList.size() > 0) {
                                String delete_sql = " set is_enable=0 where _id=" + id;
                                is_success = appDB.update("pc_driver_publish_info", delete_sql);
                                if (is_success) {
                                    json = AppJsonUtils.returnSuccessJsonString(result, "删除成功！");
                                } else {
                                    json = AppJsonUtils.returnFailJsonString(result, "删除失败！");
                                }
                                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                            } else {
                                json = AppJsonUtils.returnSuccessJsonString(result, "删除成功！");
                                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                            }
                        }

                    }
                    //寻找车主
                case "show":
                    try {
                        if (boarding_point != null && !boarding_point.isEmpty()) {
                            JSONObject boardingObject = JSONObject.parseObject(boarding_point);
                            departure_address_code = boardingObject.getIntValue("adCode");

                        }
                        if (breakout_point != null && !breakout_point.isEmpty()) {
                            JSONObject breakoutObject = JSONObject.parseObject(breakout_point);
                            destination_address_code = breakoutObject.getIntValue("adCode");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result = AppJsonUtils.getAPPDriverDepartureList(appDB, page, size, departure_address_code, destination_address_code, 0);
                    json = AppJsonUtils.returnSuccessJsonString(result, "全部出车信息获取成功");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                case "show_mine":
                    if (token != null && token.length() == 32) {
                        user_id = appDB.getIDByToken(token);
                    } else {
                        result.put("error_code", ErrorCode.getToken_expired());
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    if (user_id > 0) {
                        result = AppJsonUtils.getAPPDriverDepartureList(appDB, page, size, departure_address_code, destination_address_code, user_id);
                        json = AppJsonUtils.returnSuccessJsonString(result, "车单历史获取成功");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        result.put("error_code", ErrorCode.getToken_expired());
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                case "show_info":
                    if (token != null && token.length() == 32) {
                        user_id = appDB.getIDByToken(token);
                    } else {
                        result.put("error_code", ErrorCode.getToken_expired());
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                    String order_id = request.getParameter("order_id");
                    result = AppJsonUtils.getAPPDriverDepartureInfo(appDB, order_id, user_id);
                    String data = Utils.getJsonObject(result.toJSONString(), "driver_data");
                    if ("{}".equals(data) || null == data) {
                        json = AppJsonUtils.returnFailJsonString(result, "出车信息详情获取失败！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        json = AppJsonUtils.returnSuccessJsonString(result, "出车信息详情获取成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }

            }
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    //司机订单
    @ResponseBody
    @RequestMapping(value = "/driver/order", method = RequestMethod.POST)
    public ResponseEntity<String> grab_order(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        int user_id = 0;
        int seats = 0;
        int order_id = 0;

        String where = "";
        boolean is_success = false;
        User user = new User();
        if (request.getParameter("token") != null) {
            String token = request.getParameter("token");
            if (token != null && token.length() == 32) {
                user_id = appDB.getIDByToken(token);
                String now_where = " where _id=" + user_id;
                try {
                    user = appDB.getUserList(now_where).get(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
            } else {
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
        }
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
        try {
            String action = request.getParameter("action");
            //添加订单
            switch (action) {
                case "add":
                    String confirm_time1 = Utils.getCurrentTime();
                    where = " where user_id = " + user_id + " and departure_time>'" + confirm_time1 + "' and is_enable=1";
                    if (appDB.getAppDriverDpartureInfo(where).size() == 0) {
                        json = AppJsonUtils.returnFailJsonString(result, "请先创建车单！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    try {
                        //身份验证
                        if (user.getIs_car_owner() != 1) {
                            result.put("error_code", ErrorCode.getIs_validated());
                            json = AppJsonUtils.returnFailJsonString(result, "请先进行车主认证！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                        if (request.getParameter("order_id") != null) {
                            order_id = Integer.parseInt(request.getParameter("order_id"));
                        }
                    } catch (NumberFormatException e) {
                        user_id = 0;
                        order_id = 0;
                        e.printStackTrace();
                    }
                    if (user_id > 0) {
                        int source = 0;
                        String now_source = request.getParameter("source");
                        if (now_source != null && !now_source.isEmpty() && now_source.equals("iOS")) {
                            source = 1;
                        }

                       /* PassengerOrder order = new PassengerOrder();
                        order.setUser_id(user_id);
                        order.set_id(order_id);
                        order.setSource(source);*/
                        String passenger_order_where = " a left join pc_user b on a.user_id=b._id where a._id=" + order_id + " and is_enable=1 and order_status=0 ";//判断该单是否已经被抢
                        //检测当前乘客行程是否依旧有效
                        List<Order> passengerOrderList = appDB.getOrderReview(passenger_order_where, 1);

                        if (passengerOrderList.size() > 0) {
                            Order passengerOrder = passengerOrderList.get(0);
                            //判断车主座位数是否满足当前乘客车单的需要

                            if (passengerOrder.getUser_id() == user_id) {
                                result.put("error_code", ErrorCode.getOrder_is_self());
                                json = AppJsonUtils.returnFailJsonString(result, "抱歉，不能抢自己发的车单哦！");
                                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                            }
                            String create_time = Utils.getCurrentTime();//抢单时间
                            String departure_time = Utils.getCurrentTime().split(" ")[0];
                            String order_where = " where  user_id= " + user_id + " and create_time >= '" + departure_time + " 00:00:00'  and create_time <='" + departure_time + " 24:00:00' and order_type=2 ";
                            int total = appDB.getCount("pc_orders", order_where);
                            //规定每天可以预定5次
                            if (total <= ConfigUtils.getDriver_grad_order_counts()) {

                                Order order1 = new Order();
                                order1.setUser_id(user_id);
                                order1.setOrder_id(passengerOrder.getOrder_id());
                                order1.setSource(source);
                                order1.setOrder_status(0);
                                order1.setOrder_type(2);
                                order1.setCreate_time(create_time);
                                is_success = appDB.createOrderReview(order1);
                                /*}*/
                            } else {
                                result.put("error_code", ErrorCode.getBooking_times_limit());
                                json = AppJsonUtils.returnFailJsonString(result, "每日抢单次数达到上限！");
                                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                            }
                            if (is_success) {
                                //更改乘客行程单状态
                                String update_sql = " set order_status=1 , update_time='" + Utils.getCurrentTime() + "' where _id=" + passengerOrder.get_id();
                                appDB.update("pc_orders", update_sql);
                                //设置乘客车单状态
                                update_sql = " set order_status=1 where _id=" + passengerOrder.getOrder_id();
                                appDB.update("pc_passenger_publish_info", update_sql);
                                //通知乘客
                                 /*String driver=user.getUser_nick_name()+"("+user.getUser_mobile()+")";
                                String title="乘客";
                                String content=driver+"邀请您一同出行，请在APP中及时处理该订单，";
                                Utils.sendAllNotifyMessage(p_mobile,title,content);*/

                                String p_mobile = passengerOrder.getUser_mobile();
                                String driver = user.getUser_nick_name();
                                String content = "您的车单在" + create_time + "被" + driver + "抢单，请您及时处理！";

                                //乘客信息，司机信息，乘客订单信息
                                JSONObject driverData = AppJsonUtils.getPushObject(appDB, passengerOrder, 2);
                                driverData.put("order_status", 100);
                                //将抢单信息通知给乘客
//                                notifyPush.pinCheNotify("11",p_mobile,content,passengerOrder.get_id(),driverData,create_time);
                                int push_id = user_id;
                                int receive_id = passengerOrder.getUser_id();
                                int push_type = 11;
                                boolean is_true = appDB.createPush(passengerOrder.get_id(), push_id, receive_id, push_type, content, 11, "11.caf", driverData.toJSONString(), 1, driver, null);
                                if (is_true) {
                                    //将抢单信息通知给乘客
                                    notifyPush.pinCheNotify("11", p_mobile, content, passengerOrder.get_id(), driverData, create_time);
                                }
                                int now_id = appDB.getMaxID("_id", "pc_orders");
                                result.put("existing_id", now_id);
                                json = AppJsonUtils.returnSuccessJsonString(result, "抢单成功！");
                                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                            }
                        }
                        result.put("error_code", ErrorCode.getDeparture_order_expired());
                        json = AppJsonUtils.returnFailJsonString(result, "下手晚了，该单已被其他车主抢走了！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    result.put("error_code", ErrorCode.getParameter_wrong());
                    json = AppJsonUtils.returnFailJsonString(result, "参数有误！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                case "driver_confirm_start_car":
                    if (user_id > 0) {
                        String confirm_time = Utils.getCurrentTime();
                        String update_sql;
                        where = " where user_id=" + user_id + " and order_type=2 and order_status<3 and is_enable=1";
                        List<Order> orderList = appDB.getOrderReview(where, 0);
                        if (orderList.size() > 0) {
                            for (Order order : orderList) {
                                if (order.getOrder_status() < 2) {
                                    if (order.getOrder_status() == -1) {
                                        json = AppJsonUtils.returnFailJsonString(result, "您已经发过车了！");
                                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                                    } else {
                                        json = AppJsonUtils.returnFailJsonString(result, "不能发车哦，还有乘客没有支付呢！");
                                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                                    }
                                }

                            }
                            for (Order order : orderList) {
                                //修改车主订单为已发车
                                order_id = order.getOrder_id();
                                update_sql = " set order_status=-1 ,update_time='" + confirm_time + "' where _id=" + order.get_id();
                                appDB.update("pc_orders", update_sql);
//                                where = " where order_id="+order.getOrder_id()+" and order_type=2 and is_enable=1";
//                               Order order2 = appDB.getOrderReview(where,0).get(0);
//                                //更改乘客行程单状态
//                                update_sql=" set order_status=1 , update_time='"+Utils.getCurrentTime()+"' where _id="+order.get_id();
//                                appDB.update("pc_orders",update_sql);
                                String content = "车主" + user.getUser_nick_name() + "于" + confirm_time + "发车，请您及时乘车！";
                                List<Order> passengerOrders = appDB.getOrderReview(" where order_type =0 and  order_id ='" + order_id + "'", 0);
                                if (passengerOrders.size() > 0) {
                                    Order passengerOrder = passengerOrders.get(0);
                                    JSONObject passengerData = AppJsonUtils.getPushObject(appDB, passengerOrder, 1);
                                    int push_id = user_id;
                                    int receive_id = passengerOrder.getUser_id();
                                    int push_type = 29;
                                    List<User> passengers = appDB.getUserList(" where _id=" + passengerOrder.getUser_id());
                                    String p_mobile = "";
                                    if (passengers.size() > 0) {
                                        p_mobile = passengers.get(0).getUser_mobile();
                                    }
                                    appDB.createPush(order_id, push_id, receive_id, push_type, content, push_type, "29.caf", passengerData.toJSONString(), 1, user.getUser_nick_name(), null);
                                    notifyPush.pinCheNotify("29", p_mobile, content, order_id, passengerData, confirm_time);
                                }
                            }
                            json = AppJsonUtils.returnSuccessJsonString(result, "车主发车成功！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);

                        }
                        json = AppJsonUtils.returnFailJsonString(result, "车主发车失败！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    result.put("error_code", ErrorCode.getParameter_wrong());
                    json = AppJsonUtils.returnFailJsonString(result, "参数有误！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                //车主结束行程
                case "driver_confirm":
                    if (user_id > 0) {
                        String confirm_time = Utils.getCurrentTime();
                        String update_sql;
                        where = " where user_id=" + user_id + " and order_type=2 and is_enable=1 order by CONVERT (create_time USING gbk)COLLATE gbk_chinese_ci desc limit 1";
                        List<Order> orderList = appDB.getOrderReview(where, 0);
                        if (orderList.size() > 0) {
                            for (Order order : orderList) {

                                update_sql = " set order_status=3 ,update_time='" + confirm_time + "' where _id=" + order.get_id();
                                appDB.update("pc_orders", update_sql);
                                update_sql = " set order_status=4, update_time='" + Utils.getCurrentTime() + "' where order_id=" + order.getOrder_id() + " and order_type=0";
                                appDB.update("pc_orders", update_sql);
                                update_sql = " set is_complete=1 where action_type=0 and order_id=" + order.getOrder_id();
                                appDB.update("pay_cash_log", update_sql);

                            }
                            //设置乘客车单不可用（完成）
                            where = " set is_enable=0 where user_id =" + user_id + " order by CONVERT (create_time USING gbk)COLLATE gbk_chinese_ci desc limit 1";
                            appDB.update("pc_driver_publish_info", where);
                        }
                        json = AppJsonUtils.returnSuccessJsonString(result, "结束行程成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    result.put("error_code", ErrorCode.getParameter_wrong());
                    json = AppJsonUtils.returnFailJsonString(result, "参数有误！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                case "passenger_confirm":
                    if (user_id > 0) {
                        order_id = Integer.parseInt(request.getParameter("order_id"));//司机订单id（乘客订单中的grab_id）
                        String status = request.getParameter("status");
                        String where_order = " a left join pc_user b on a.user_id=b._id where a.is_enable=1 and a._id=" + order_id;
                        List<Order> orderList = appDB.getOrderReview(where_order, 1);
                        String d_mobile = "";
                        List<Order> passengerOrderList;
                        int passenger_order_id = 0;
                        int id = 0;
                        if (orderList.size() > 0) {
                            passenger_order_id = orderList.get(0).getOrder_id();//得到乘客出行单id
                            d_mobile = orderList.get(0).getUser_mobile();
                            id = orderList.get(0).getUser_id();
                            where_order = " a left join pc_passenger_publish_info b on a.order_id=b._id where b._id=" + passenger_order_id + " and b.is_enable=1 and b.order_status=1 and departure_time>='" + Utils.getCurrentTime() + "'";
                            passengerOrderList = appDB.getOrderReview(where_order, 2);
                        } else {
                            result.put("error_code", ErrorCode.getBooking_order_is_not_existing());
                            json = AppJsonUtils.returnFailJsonString(result, "司机已取消抢单！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                        if (passengerOrderList.size() != 0) {
                            String update_sql;
                            //司机基本信息
                            Order passengerOrder = passengerOrderList.get(0);//乘客出行详情
                            /*String date=passengerOrder.getDeparture_time().split(" ")[0].split("-")[1]+"月"+passengerOrder.getDeparture_time().split(" ")[0].split("-")[2]+"日";
                            String passenger=user.getUser_name()+"("+user.getUser_mobile()+")";*/
                            if (status != null) {
                                String confirm_time = Utils.getCurrentTime();
                                if (status.trim().equals("1")) {
                                    //操作，更新2条数据状态
                                    update_sql = " set order_status=1 ,update_time='" + confirm_time + "' where _id=" + order_id;
                                    appDB.update("pc_orders", update_sql);//司机抢单记录状态
                                    update_sql = " set order_status=2 ,update_time='" + confirm_time + "' where order_id=" + passengerOrder.getOrder_id() + " and user_id=" + user_id;
                                    appDB.update("pc_orders", update_sql);//乘客订单记录状态
                                    //todo:乘客推送
                                  /*  String title="乘客";
                                    String content=passenger+"确认了您"+date+"行程的座位订单，";
                                    Utils.sendAllNotifyMessage(user.getUser_mobile(),title,content);*/
                                    String content = "乘客" + user.getUser_nick_name() + "在" + confirm_time + "确认了您的抢单，等待乘客支付！";
                                    //乘客信息，司机信息，乘客订单信息
                                    JSONObject passengerData = AppJsonUtils.getPushObject(appDB, passengerOrder, 1);
                                    int push_type = 21;
                                    int push_id = user_id;
                                    boolean is_true = appDB.createPush(passengerOrder.get_id(), push_id, id, push_type, content, push_type, push_type + ".caf", passengerData.toJSONString(), 1, user.getUser_nick_name(), "");
                                    if (is_true) {
                                        notifyPush.pinCheNotify("21", d_mobile, content, order_id, passengerData, confirm_time);
                                    }
                                } else {
                                    update_sql = " set order_status=4 ,update_time='" + confirm_time + "' where _id=" + order_id;
                                    appDB.update("pc_orders", update_sql);//司机抢单记录状态
                                    //拒绝后重新进入等待司机抢单状态
                                    update_sql = " set order_status=0 ,update_time='" + confirm_time + "' where order_id=" + passengerOrder.getOrder_id() + " and order_type=0";
                                    appDB.update("pc_orders", update_sql);//乘客订单记录状态

                                    update_sql = " set order_status=0 where _id=" + passengerOrder.getOrder_id();
                                    appDB.update("pc_passenger_publish_info", update_sql);//乘客出行单状态
                                    update_sql = " set is_enable=0 where order_type=2 and order_id=" + passengerOrder.getOrder_id() + " order By create_time DESC limit 1";
                                    appDB.update("pc_orders", update_sql);

                                    //todo:乘客拒绝推送
                                   /* String title="乘客";
                                    String content=passenger+"拒绝了您"+date+"行程，请搜索其他同行车主，";
                                    Utils.sendAllNotifyMessage(user.getUser_mobile(),title,content);
                                    JSONObject pushObject=new JSONObject();*/

                                    //乘客信息，司机信息，乘客订单信息
                                    String content = "乘客" + user.getUser_nick_name() + "在" + confirm_time + "拒绝了您的抢单！";
                                    JSONObject passengerData = AppJsonUtils.getPushObject(appDB, passengerOrder, 1);
                                    int push_type = 22;
                                    int push_id = user_id;
                                    boolean is_true = appDB.createPush(passengerOrder.get_id(), push_id, id, push_type, content, push_type, push_type + ".caf", passengerData.toJSONString(), 1, user.getUser_nick_name(), null);
                                    if (is_true) {
                                        notifyPush.pinCheNotify("22", d_mobile, content, order_id, passengerData, confirm_time);
                                    }
                                }
                                result.put("action_time", confirm_time);
                                json = AppJsonUtils.returnSuccessJsonString(result, "处理成功！");
                                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                            }
                            result.put("error_code", ErrorCode.getBooking_order_is_not_existing());
                            json = AppJsonUtils.returnFailJsonString(result, "参数错误！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            result.put("error_code", ErrorCode.getParameter_wrong());
                            json = AppJsonUtils.returnFailJsonString(result, "乘客已取消该订单或者订单已失效！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                    }
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                case "delete":
                    if (user_id > 0) {
                        order_id = Integer.parseInt(request.getParameter("order_id"));//抢单记录id
                       /* String user_where = " where _id=" + user_id;
                        String d_mobile = appDB.getUserList(user_where).get(0).getUser_mobile();*/
                        String order_where = " where _id=" + order_id + " and user_id=" + user_id;
                        List<Order> orderList = appDB.getOrderReview(order_where, 0);
                        if (orderList.size() > 0) {
                            Order order = orderList.get(0);
                            //检测乘客是否支付
                            if (order.getOrder_status() == 2 || order.getOrder_status() == -1) {
                                result.put("error_code", ErrorCode.getOrder_grabed_unable_cancle());
                                json = AppJsonUtils.returnFailJsonString(result, "乘客已支付，抢单无法取消，如有特殊原因，建议联系乘客主动取消！");
                                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                            }
                            String update_sql = " set is_enable=0 where _id=" + order_id;
                            appDB.update("pc_orders", update_sql);
                            //查看乘客发布的出行信息是否还存在
                            order_where = " a right join pc_passenger_publish_info b on a.order_id=b._id where a.order_type=0 and  a.order_id=" + order.getOrder_id() + " and a.is_enable=1 and departure_time>='" + Utils.getCurrentTime() + "'";
                            List<Order> passengerDepartureInfo = appDB.getOrderReview(order_where, 2);
                            if (passengerDepartureInfo.size() == 0) {
                                //不做通知
                                json = AppJsonUtils.returnSuccessJsonString(result, "删除成功！");
                                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                            }
                            //司机取消抢单，乘客出行状态置为0
                           /* if(order.getOrder_status()<2)
                            {*/
                            where = " where _id=" + passengerDepartureInfo.get(0).getUser_id();
                            User passenger = appDB.getUserList(where).get(0);
                            //乘客未支付
                            String current_time = Utils.getCurrentTime();
                            Order passengerOrder = passengerDepartureInfo.get(0);
                            update_sql = " set order_status=0 ,update_time='" + current_time + "' where order_id=" + passengerOrder.getOrder_id() + " and user_id=" + passengerOrder.getUser_id();
                            appDB.update("pc_orders", update_sql);

                            update_sql = " set order_status=0 where _id=" + passengerOrder.getOrder_id();
                            appDB.update("pc_passenger_publish_info", update_sql);
                            //通知乘客

                            String p_mobile = passenger.getUser_mobile();
                            String driver = user.getUser_nick_name();
                            String content = "您的车单在" + current_time + "被" + driver + "取消抢单，请您耐心等待其他车主接单！";

                            //乘客信息，司机信息，乘客订单信息
                            JSONObject driverData = AppJsonUtils.getPushObject(appDB, passengerOrder, 2);
                            int push_type = 12;
                            int push_id = user_id;
                            int receive_id = passengerOrder.getUser_id();
                            boolean is_true = appDB.createPush(passengerOrder.get_id(), push_id, receive_id, push_type, content, push_type, push_type + ".caf", driverData.toJSONString(), 1, driver, null);
                            if (is_true) {
                                notifyPush.pinCheNotify("12", p_mobile, content, passengerOrder.get_id(), driverData, current_time);
                            }
                            /*}else if(order.getOrder_status()==2)
                            {
                                String where= " where _id="+passengerDepartureInfo.get(0).getUser_id();
                                User passenger=appDB.getUserList(where).get(0);

                                //乘客已完成支付
                                String current_time=Utils.getCurrentTime();
                                Order passengerOrder=passengerDepartureInfo.get(0);
                                *//*update_sql=" set order_status=-1 ,update_time='"+current_time+"' where order_id="+passengerOrder.getOrder_id()+" and user_id="+passengerOrder.getUser_id();
                                appDB.update("pc_orders",update_sql);*//*

                                //通知乘客
                                String  p_mobile=passenger.getUser_mobile();
                                String driver=user.getUser_nick_name();
                                String content="您的车单在"+current_time+"被"+driver+"取消抢单，请在“我的订单”中申请退款！";

                                //乘客信息，司机信息，乘客订单信息
                                JSONObject driverData=AppJsonUtils.getPushObject(appDB,passengerOrder,2);
                                notifyPush.pinCheNotify("31",p_mobile,content,passengerOrder.get_id(),driverData);
                            }*/

                        }
                        json = AppJsonUtils.returnSuccessJsonString(result, "删除成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                //订单状态
                case "show_my_orders":
//                    int flag=0;
//                    if(request.getParameter("flag")!=null){
//                        try {
//                            flag=Integer.parseInt(request.getParameter("flag"));
//                        } catch (NumberFormatException e) {
//                            flag=0;
//                            e.printStackTrace();
//                        }
//                    }
                    if (user_id > 0) {
                        result = AppJsonUtils.getMyGrabOrderList(appDB, page, size, user_id);
                        json = AppJsonUtils.returnSuccessJsonString(result, "司机抢单列表获取成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                case "show_my_order_info":
                    order_id = Integer.parseInt(request.getParameter("order_id"));
                    result = AppJsonUtils.getMyGrabOrderInfo(appDB, order_id);
                    String data = Utils.getJsonObject(result.toJSONString(), "passenger_data");
                    if (null == data || "{}".equals(data)) {
                        json = AppJsonUtils.returnFailJsonString(result, "司机抢单详情获取失效！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        json = AppJsonUtils.returnSuccessJsonString(result, "司机抢单详情获取成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
            }
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    //车主状态
    @ResponseBody
    @RequestMapping(value = "/driver/status", method = RequestMethod.POST)
    public ResponseEntity<String> status(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        int user_id = 0;
        int seats = 0;
        int order_id = 0;
        boolean is_success = false;
        User user = new User();
        if (request.getParameter("token") != null) {
            String token = request.getParameter("token");
            if (token != null && token.length() == 32) {
                user_id = appDB.getIDByToken(token);
                String now_where = " where _id=" + user_id;
                try {
                    user = appDB.getUserList(now_where).get(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
            } else {
                result.put("error_code", ErrorCode.getToken_expired());
                json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
            }
        }
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
        try {
            String action = request.getParameter("action");
            switch (action) {
                //车单状态一
                case "show":
                    if (user_id > 0) {
                        order_id = Integer.parseInt(request.getParameter("order_id"));

                        result = AppJsonUtils.getMyDriverStatusList(appDB, page, size, user_id, order_id);
                        if (null != Utils.getJsonObject(result.toJSONString(), "data")) {
                            json = AppJsonUtils.returnSuccessJsonString(result, "车主列表获取成功！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            json = AppJsonUtils.returnSuccessJsonString(result, "此订单不可用！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                    }
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);

            }
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error_code", ErrorCode.getParameter_wrong());
            json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/owner/type", method = RequestMethod.POST)
    public ResponseEntity<String> type(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        int user_id = 0;
        String token = request.getParameter("token");
        if (token != null && token.length() == 32) {
            user_id = appDB.getIDByToken(token);

        } else {
            result.put("error_code", ErrorCode.getToken_expired());
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }
        String where = " where user_id=" + user_id + " and is_enable=1 order by CONVERT (update_time USING gbk)COLLATE gbk_chinese_ci desc limit 1";
        Order order = appDB.getOrderReview(where, 0).get(0);
        result.put("status", order.getOrder_status());
        json = AppJsonUtils.returnSuccessJsonString(result, "获取成功！");
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }
}
