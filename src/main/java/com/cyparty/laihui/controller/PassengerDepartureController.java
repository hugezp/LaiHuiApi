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
import java.util.List;

/**
 * Created by zhu on 2016/5/11.
 */
@Controller
@ResponseBody
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class PassengerDepartureController {
    @Autowired
    AppDB appDB;
    @Autowired
    OssUtil ossUtil;
    @Autowired
    NotifyPush notifyPush;

    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/now/test", method = RequestMethod.GET)
    public String getTest(HttpServletRequest request) {
        notifyPush.testAsyncMethod();
        return "test";
    }

    /**
     * 计算乘客价格模块
     *
     * @param request
     * @return
     */
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/distance/price", produces = "application/json; charset=utf-8")
    public String getPrice(HttpServletRequest request) {
        String origin_location = request.getParameter("origin_location");
        String destination_location = request.getParameter("destination_location");
        String booking_seats = request.getParameter("booking_seats");
        int person = 1;
        if (booking_seats != null && !booking_seats.isEmpty()) {
            try {
                person = Integer.parseInt(booking_seats);
            } catch (NumberFormatException e) {
                person = 1;
                e.printStackTrace();
            }
        }
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
            double start_price = 0;
            double  price = distance * 3.3 / 10000f;
            if (distance<=200000){
                start_price = 10.0;

            }
            double last_price = start_price + price*person;
            //测试
//            double  price =0.01;
//            double last_price =0.01;


            DecimalFormat df = new DecimalFormat("######0.00");
            double average = price * 1000f / distance;
//            resultObject.put("price",0.01);
//            resultObject.put("total_price",0.01);
            resultObject.put("price", new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            resultObject.put("total_price", new BigDecimal(last_price).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            resultObject.put("cost_time", duration / 60 + "分钟");
            resultObject.put("distance", distance / 1000);
            resultObject.put("average", new BigDecimal(df.format(average)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        }
        return resultObject.toString();
    }

    /**
     * 乘客车单模块
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/passenger/departure", method = RequestMethod.POST)
    public ResponseEntity<String> booking(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
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
            boolean is_success = true;
            int seats = 0;
            int user_id = 0;
            int order_id = 0;
            String token = null;

            if (request.getParameter("token") != null) {
                try {
                    token = request.getParameter("token");
                    user_id = appDB.getIDByToken(token);
                } catch (Exception e) {
                    user_id = 0;
                    e.printStackTrace();
                }
            }
            String boarding_point = request.getParameter("boarding_point");
            String breakout_point = request.getParameter("breakout_point");
            int departure_address_code = 0;
            int departure_city_code = 0;
            int destination_address_code = 0;
            int destination_city_code = 0;
            //发布车单
            switch (action) {
                case "add":
                    try {
                        if (request.getParameter("booking_seats") != null) {
                            seats = Integer.parseInt(request.getParameter("booking_seats"));
                        }
                    } catch (NumberFormatException e) {
                        seats = 0;
                        user_id = 0;
                        e.printStackTrace();
                    }
                    if (user_id > 0) {
                        String user_where = " where _id=" + user_id;
                        User user = appDB.getUserList(user_where).get(0);
                        if (user.getIs_validated() == 1) {
                            String start_time = request.getParameter("departure_time");//出发时间
                            int source = 0;
                            if (request.getParameter("source") != null && request.getParameter("source").equals("iOS")) {
                                source = 1;
                            }
                            String remark = "乘客轻装简行";
                            if (request.getParameter("remark") != null && !request.getParameter("remark").isEmpty()) {
                                remark = request.getParameter("remark");
                            }
                            double price = 0;
                            try {
                                price = Double.parseDouble(request.getParameter("price"));//价格
                            } catch (NumberFormatException e) {
                                price = 0;
                                e.printStackTrace();
                            }
                            String description = request.getParameter("description");
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
                                json = AppJsonUtils.returnFailJsonString(result, "创建失败！");
                                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                            }
                            PassengerOrder order = new PassengerOrder();

                            order.setPay_money(price);
                            order.setSource(source);
                            order.setUser_id(user_id);
                            order.setStart_time(start_time);
                            order.setSeats(seats);
                            order.setBoarding_point(boarding_point);
                            order.setDeparture_city_code(departure_city_code);
                            order.setDeparture_address_code(departure_address_code);
                            order.setBreakout_point(breakout_point);
                            order.setDestination_city_code(destination_city_code);
                            order.setDestination_address_code(destination_address_code);
                            order.setDescription(description);
                            order.setOrder_type(0);
                            order.setRemark(remark);

                            String trade_no = Utils.getTimestamp() + Utils.random(2);//15
                            order.setPay_num(trade_no);
                            //判断是否已存在进行中车单
                            String order_where = " a right join pc_passenger_publish_info b on a.order_id=b._id where  b.is_enable=1 and a.order_status<4 and a.order_status>=0 and order_type=0 and a.user_id=" + user_id;
                            List<Order> passengerDepartureInfo = appDB.getOrderReview(order_where, 2);
                            if (passengerDepartureInfo.size() > 0) {
                                json = AppJsonUtils.returnFailJsonString(result, "太贪心了，您尚有未完成的行程！");
                                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                            } else {
                                //查询今日已发布次数
                                String now_time = Utils.getCurrentTime().split(" ")[0] + " 00:00:00";
                                String now_where = " where user_id=" + user_id + " and create_time >='" + now_time + "' and order_type=0 ";
                                List<Order> todayOrderList = appDB.getOrderReview(now_where, 0);
                                if (todayOrderList.size() < ConfigUtils.getDriver_departure_counts()) {
                                    //将车单添加到数据库中
                                    is_success = appDB.createPassengerDeparture(order);
                                } else {
                                    json = AppJsonUtils.returnFailJsonString(result, "每日发布行程次数为" + ConfigUtils.getPassenger_departure_counts() + "次，您今日发布次数已达到上限！");
                                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                                }
                            }
                            if (is_success) {
                                //记录乘客发布
                                String confirm_time = Utils.getCurrentTime();
                                //乘客车单ID
                                int id = appDB.getMaxID("_id", "pc_passenger_publish_info");
                                Order order1 = new Order();
                                order1.setUser_id(user_id);
                                order1.setOrder_id(id);
                                order1.setSource(source);
                                order1.setOrder_status(0);
                                order1.setOrder_type(0);
                                order1.setIs_complete(0);
                                order1.setCreate_time(confirm_time);
                                order1.setRemark(remark);
                                appDB.createOrderReview(order1);
                                //乘客车单ID
                                result.put("car_id", id);
                                result.put("boarding_point", boarding_point);
                                result.put("breakout_point", breakout_point);
                                result.put("departure_time", start_time);
                                json = AppJsonUtils.returnSuccessJsonString(result, "乘客行程单创建成功！");
                                return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                            }
                            json = AppJsonUtils.returnFailJsonString(result, "乘客行程单创建失败！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            result.put("error_code", ErrorCode.getIs_validated());
                            json = AppJsonUtils.returnFailJsonString(result, "请先进行乘客身份认证！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        }
                    }
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                //向同路线的车主推送车单消息
                case "push":
                    if (user_id > 0) {
                        String user_where = " where _id=" + user_id;
                        //得到乘客自身信息
                        User user = appDB.getUserList(user_where).get(0);
                        //根据乘客的车单信息
                        PassengerOrder order = appDB.getPassengerDepartureInfo(" where user_id =" + user_id + " order by create_time DESC limit 1").get(0);
                        Order order1 = appDB.getOrderReview(" where order_id=" + order.get_id() + " and user_id=" + user_id + " and is_enable=1 and order_type=0", 0).get(0);
                        String boardingPoint = order.getBoarding_point();
                        String breakoutPoint = order.getBreakout_point();
                        JSONObject boardingObject = JSONObject.parseObject(boardingPoint);
                        departure_address_code = boardingObject.getIntValue("adCode");
                        departure_city_code = Integer.parseInt((departure_address_code + "").substring(0, 4) + "00");
                        JSONObject breakoutObject = JSONObject.parseObject(breakoutPoint);
                        destination_address_code = breakoutObject.getIntValue("adCode");
                        destination_city_code = Integer.parseInt((destination_address_code + "").substring(0, 4) + "00");
                        //筛选同路线车主
                        String notify_where = " where is_enable=1 and departure_city_code=" + departure_city_code + " and destination_city_code=" + destination_city_code + " group by mobile";
                        List<DepartureInfo> departureInfoList = appDB.getAppDriverDpartureInfo(notify_where);
                        String boarding_address = Utils.getJsonObject(boardingPoint, "name");
                        String boarding_city = Utils.getJsonObject(boardingPoint, "city");
                        String breakout_address = Utils.getJsonObject(breakoutPoint, "name");
                        String breakout_city = Utils.getJsonObject(breakoutPoint, "city");
                        if (departureInfoList.size() > 0) {
                            for (DepartureInfo departureInfo : departureInfoList) {
                                String content = "顺路乘客" + user.getUser_nick_name() + "发布了 (" + order.getDeparture_time() + ") 从" + boarding_city + boarding_address + "到" + breakout_city + breakout_address + "的出行信息，快来抢单吧！";
                                //乘客信息，司机信息，乘客订单信息
                                //信息推送
                                JSONObject passengerData = AppJsonUtils.getPushObject(appDB, order1, 1);
                                try {
                                    notifyPush.pinCheNotify("25", departureInfo.getMobile(), content, order.get_id(), passengerData, Utils.getCurrentTime());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            notify_where = " where departure_address = '"+ boarding_address +"' and destinat_address='"+ breakout_address+ "' and is_enable=1";
                            List<CommonRoute> commonRouteList = appDB.getCommonRoute(notify_where);
                            if (commonRouteList.size()>0){
                                for (CommonRoute commonRoute : commonRouteList){
                                    notify_where = " where _id = "+ commonRoute.getUser_id();
                                    User user1 = appDB.getUserList(notify_where).get(0);
                                    String content = "顺路乘客" + user.getUser_nick_name() + "发布了 (" + order.getDeparture_time() + ") 从" + boarding_city + boarding_address + "到" + breakout_city + breakout_address + "的出行信息，与您经常驾驶的路线相符，快来抢单吧！";
                                    JSONObject passengerData = AppJsonUtils.getPushObject(appDB, order1, 1);
                                    try {
                                        notifyPush.pinCheNotify("25", user1.getUser_mobile(), content, order.get_id(), passengerData, Utils.getCurrentTime());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            json = AppJsonUtils.returnSuccessJsonString(result, "推送发送成功！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            json = AppJsonUtils.returnFailJsonString(result, "推送发送失败！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        result.put("error_code", ErrorCode.getToken_expired());
                        json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
                    }

                    //根据出发地或者目的地匹配
                    //寻找乘客
                case "show":
                    try {
                        if (boarding_point != null) {
                            JSONObject boardingObject = JSONObject.parseObject(boarding_point);
                            departure_address_code = boardingObject.getIntValue("adCode");
                        }
                        if (breakout_point != null) {
                            JSONObject breakoutObject = JSONObject.parseObject(breakout_point);
                            destination_address_code = breakoutObject.getIntValue("adCode");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    result = AppJsonUtils.getPassengerDepartureList(appDB, page, size, departure_address_code, destination_address_code, 0);
                    json = AppJsonUtils.returnSuccessJsonString(result, "乘客出行信息获取成功");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                //精确查询（具体到某个乘客）
                case "show_info":
                    //获取乘客车单ID
                    order_id = Integer.parseInt(request.getParameter("order_id"));
                    result = AppJsonUtils.getPassengerDepartureList(appDB, page, size, departure_address_code, destination_address_code, order_id);
                    String data = Utils.getJsonObject(result.toJSONString(), "data");
                    if ("[]".equals(data) || null == data) {
                        json = AppJsonUtils.returnFailJsonString(result, "乘客出行信息详情获取失败！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    } else {
                        json = AppJsonUtils.returnSuccessJsonString(result, "乘客出行信息详情获取成功！");
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

    /**
     * 乘客订单模块
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/passenger/order", method = RequestMethod.POST)
    public ResponseEntity<String> passenger_list(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        int user_id = 0;
        int order_id = 0;
        User user = new User();
        if (request.getParameter("token") != null) {
            String token = request.getParameter("token");
            if (token != null && token.length() == 32) {
                user_id = appDB.getIDByToken(token);
                String now_where = " where _id=" + user_id;
                try {
                    List<User> userList = appDB.getUserList(now_where);
                    if (userList.size() > 0) {
                        user = userList.get(0);
                    }
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
                //乘客取消订单
                case "delete":
                    if (user_id > 0) {
                        //乘客删除，调用乘客订单数据中record_id（乘客车单id）
                        order_id = Integer.parseInt(request.getParameter("order_id"));
                        String flag = request.getParameter("flag");
                        String order_where = " a left join pc_user b on a.user_id=b._id where order_type=0 and a.order_id=" + order_id + " and user_id=" + user_id;
                        //获得订单列表
                        List<Order> orderList = appDB.getOrderReview(order_where, 1);
                        if (flag == null || flag.isEmpty()) {
                            json = AppJsonUtils.returnFailJsonString(result, "参数错误！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            if (flag.equals("0")) {//取消订单
                                if (orderList.size() > 0) {
                                    //乘客订单记录表
                                    Order order = orderList.get(0);
                                    String confirm_time = Utils.getCurrentTime();
                                    //更新订单记录表（设为不可用）
                                    String update_sql = " set is_enable=0,update_time='" + confirm_time + "' where _id=" + order.get_id();
                                    appDB.update("pc_orders", update_sql);
                                    //更新乘客车单表（设为不可用）
                                    update_sql = " set is_enable=0 where _id=" + order_id;
                                    appDB.update("pc_passenger_publish_info", update_sql);
                                    //更新车主抢单记录表（设为乘客订单失效）
                                    update_sql = " set order_status=5,update_time='" + confirm_time + "' where order_id=" + order.getOrder_id() + " and order_type=2 ";
                                    appDB.update("pc_orders", update_sql);

                                    String driver_where = " a left join pc_user b on a.user_id=b._id where order_id=" + order.getOrder_id() + " and order_type=2 and order_status<=2 ";
                                    String d_mobile = "";
                                    //相关车主的订单记录列表
                                    List<Order> driverList = appDB.getOrderReview(driver_where, 1);
                                    if (driverList.size() > 0) {
                                        d_mobile = driverList.get(0).getUser_mobile();
                                        //向车主推送乘客取消订单消息
                                        String content = "乘客" + user.getUser_nick_name() + "在" + confirm_time + "取消了本次行程，对您造成的不便，敬请谅解！";
                                        JSONObject passengerData = AppJsonUtils.getPushObject(appDB, order, 1);

                                        //保存推送消息
                                        int push_id = user_id;
                                        int receive_id = driverList.get(0).getUser_id();
                                        int push_type = 23;
                                        appDB.createPush(order_id, push_id, receive_id, push_type, content, 23, "23.caf", passengerData.toJSONString(), 1, user.getUser_nick_name(),null);
                                        notifyPush.pinCheNotify("23", d_mobile, content, order_id, passengerData, confirm_time);
                                    }
                                    json = AppJsonUtils.returnSuccessJsonString(result, "订单取消成功！");
                                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                                }
                            }
                        }
                    }
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                //乘客订单状态.
                case "show_my_orders":
                    if (user_id > 0) {
                        result = AppJsonUtils.getMyBookingOrderList(appDB, page, size, user_id);
                        json = AppJsonUtils.returnSuccessJsonString(result, "乘客订单列表信息获取成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);

                //乘客订单详情
                case "show_my_order_info":
                    //乘客订单记录id
                    order_id = Integer.parseInt(request.getParameter("order_id"));

                    result = AppJsonUtils.getMyBookingOrderInfo(appDB, order_id);
                    String data = Utils.getJsonObject(result.toJSONString(), "passenger_data");
                    String data1= Utils.getJsonObject(result.toJSONString(),"driver_data");
                    if ("{}".equals(data) || null == data || result == null || result.equals("{}")||"{}".equals(data) || null == data) {
                        json = AppJsonUtils.returnFailJsonString(result, "乘客订单已失效！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);

                    } else {
                        json = AppJsonUtils.returnSuccessJsonString(result, "乘客订单详情获取成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    //确认到达（未使用）
                case "arrive_confirm":
                    if (user_id > 0) {
                        //乘客车单id
                        String confirm_time = Utils.getCurrentTime();
                        order_id = Integer.parseInt(request.getParameter("order_id"));
                        String where = " where order_type=0 and order_id=" + order_id + " and user_id=" + user_id;
                        List<Order> orderList = appDB.getOrderReview(where, 0);
                        Order order = new Order();
                        if (orderList.size() > 0) {
                            order = orderList.get(0);
                        }
                        where = " a left join pc_user b on a.user_id=b._id where order_type=2 and order_id=" + order_id + " and order_status=2";
                        List<Order> driverList = appDB.getOrderReview(where, 1);
                        String d_mobile = "";
                        //相关车主的订单id
                        int grab_id = 0;
                        if (driverList.size() > 0) {
                            grab_id = driverList.get(0).get_id();
                            d_mobile = driverList.get(0).getUser_mobile();
                        }
                        String update_sql = " set order_status=4,update_time='" + confirm_time + "' where order_type=0 and user_id=" + user_id + " and order_id=" + order_id;
                        appDB.update("pc_orders", update_sql);
                        update_sql = " set order_status=3,update_time='" + confirm_time + "' where order_type=2 and order_status=2 and order_id=" + order_id;
                        appDB.update("pc_orders", update_sql);
                        update_sql = " set is_complete=1 where action_type=0 and order_id=" + order_id;
                        appDB.update("pay_cash_log", update_sql);


                        String content = "乘客" + user.getUser_nick_name() + "在" + confirm_time + "确认到达了目的地！";
                        JSONObject passengerData = AppJsonUtils.getPushObject(appDB, order, 1);
                        int push_id = user_id;
                        int receive_id = driverList.get(0).getUser_id();
                        int push_type = 24;
                        appDB.createPush(grab_id, push_id, receive_id, push_type, content, push_type, "24.caf", passengerData.toJSONString(), 1, user.getUser_nick_name(),null);
                        notifyPush.pinCheNotify("24", d_mobile, content, grab_id, passengerData, confirm_time);

                        json = AppJsonUtils.returnSuccessJsonString(result, "确认到达成功！");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    result.put("error_code", ErrorCode.getToken_expired());
                    json = AppJsonUtils.returnFailJsonString(result, "非法token！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);

                //乘客邀请车主抢单
                case "invite_driver":
                    String where = " where user_id = " + user_id + " and order_type=0 and order_status=0 and is_enable=1";
                    List<Order> orderList1 = appDB.getOrderReview(where, 0);
                    if (orderList1.size() == 0) {
                        json = AppJsonUtils.returnFailJsonString(result, "请您创建行程后再邀请车主");
                        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                    }
                    boolean is_enable = false;
                    if (user_id > 0) {
                        //获取系统现在时间
                        String confirm_time = Utils.getCurrentTime();
                        where = " where user_id =" + user_id + " and is_enable =1 order by CONVERT (create_time USING gbk)COLLATE gbk_chinese_ci desc limit 1";
                        Order passenger = appDB.getOrderReview(where, 0).get(0);
                        //获取车主手机号
                        String driver_mobile = request.getParameter("driver_mobile");
                        //获取车主id
                        int driver_id = Integer.parseInt(request.getParameter("driver_id"));
                        where = " where a._id =" + driver_id;
                        int driver_user_id = appDB.getAppDriverDpartureInfo(where).get(0).getUser_id();
                        //乘客车单id
                        int grab_id = passenger.getOrder_id();

                        String content = "乘客" + user.getUser_nick_name() + "在" + confirm_time + "邀请您抢单！";
                        JSONObject passengerData = new JSONObject();
                        passengerData.put("user_avatar", passenger.getUser_avatar());
                        passengerData.put("user_name", passenger.getUser_name());
                        passengerData.put("boarding_point", passenger.getBoarding_point());
                        passengerData.put("breakout_point", passenger.getBreakout_point());
                        passengerData.put("remark", passenger.getRemark());
                        passengerData.put("departure", passenger.getDeparture_time());
                        passengerData.put("create_time", passenger.getCreate_time());
                        passengerData.put("mobile", passenger.getUser_mobile());
                        passengerData.put("order_status", passenger.getOrder_status());

                        PCCount driverPCCount = AppJsonUtils.getPCCount(appDB, passenger.getUser_id());
                        passengerData.put("pc_count", driverPCCount.getTotal());
                        where = " where passenger_id=" + user_id + " and driver_id=" + driver_user_id;
                        //获取邀请记录
                        List<InviteIimit> inviteIimit = appDB.getinviteIimit(where);
                        if (inviteIimit.size() > 0) {
                            //查询上次邀请距离这次邀请的时间
                            if (DateUtils.getTimesToNow1(inviteIimit.get(0).getInvite_time()) > 15) {
                                where = " set invite_time='" + confirm_time + "'where passenger_id=" + user_id + " and driver_id=" + driver_id;
                                appDB.update("pc_invite_limit", where);
                                is_enable = true;
                            }
                        } else {
                            //添加邀请记录
                            appDB.createInviteIimit(user_id, driver_user_id, confirm_time);
                            is_enable = true;
                        }
                        if (is_enable) {
                            //保存推送消息
                            int push_id = user_id;
                            int receive_id = driver_user_id;
                            int push_type = 28;
                            appDB.createPush(grab_id, push_id, receive_id, push_type, content, 28, "28.caf", passengerData.toJSONString(), 1, user.getUser_nick_name(),null);
                            //将邀请消息推送给车主
                            notifyPush.pinCheNotify("28", driver_mobile, content, grab_id, passengerData, confirm_time);

                            json = AppJsonUtils.returnSuccessJsonString(result, "邀请抢单成功！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            json = AppJsonUtils.returnFailJsonString(result, "邀请过于频繁，请稍后再试！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
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

    /**
     * 乘客车单状态模块
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/passenger/status", method = RequestMethod.POST)
    public ResponseEntity<String> status(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        int user_id = 0;
        User user = new User();
        if (request.getParameter("token") != null) {
            String token = request.getParameter("token");
            if (token != null && token.length() == 32) {
                user_id = appDB.getIDByToken(token);
                String now_where = " where _id=" + user_id;
                try {
                    List<User> userList = appDB.getUserList(now_where);
                    if (userList.size() > 0) {
                        user = userList.get(0);
                    }
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
                case "show":
                    if (user_id > 0) {
                        result = AppJsonUtils.getMyPassengerStatusList(appDB, page, size, user_id);
                        if (null != result) {
                            json = AppJsonUtils.returnSuccessJsonString(result, "乘客车单列表信息获取成功！");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                        } else {
                            //和上一版本不一样，需谨慎
                            json = AppJsonUtils.returnFailJsonString(result, "暂无数据，请您稍后查询");
                            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
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

    /**
     * 乘客提醒司机发车模块
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/remind/driver/start", method = RequestMethod.POST)
    public ResponseEntity<String> start(HttpServletRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        String confirm_time = Utils.getCurrentTime();
        int user_id = 0;
        User user = new User();
        String token = request.getParameter("token");
        if (token != null && token.length() == 32) {
            user_id = appDB.getIDByToken(token);
            String now_where = " where _id=" + user_id;
            try {
                List<User> userList = appDB.getUserList(now_where);
                if (userList.size() > 0) {
                    user = userList.get(0);
                }
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
        if (user_id > 0) {
            try {
                //获取乘客订单id
                int id = Integer.parseInt(request.getParameter("id"));
                //获取车主手机号
                String driver_mobile = request.getParameter("driver_mobile");
                String content = "乘客" + user.getUser_nick_name() + "在" + confirm_time + "提醒您发车！";
                JSONObject passengerData = new JSONObject();
                passengerData.put("order_status", 100);
                int push_type =27;
                List<User> drivers = appDB.getUserList(" where user_mobile ='"+driver_mobile+"'");
                if(drivers.size()>0){
                    appDB.createPush(id, user_id, drivers.get(0).getUser_id(), push_type, content, push_type, "27.caf", passengerData.toJSONString(), 1, user.getUser_nick_name(),null);
                    notifyPush.pinCheNotify("27", driver_mobile, content, id, passengerData, confirm_time);

                    json = AppJsonUtils.returnSuccessJsonString(result, "提醒发车成功！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }else{
                    json = AppJsonUtils.returnFailJsonString(result, "提醒发车失败！");
                    return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.put("error_code", ErrorCode.getParameter_wrong());
                json = AppJsonUtils.returnFailJsonString(result, "获取参数错误");
                return new ResponseEntity<>(json, responseHeaders, HttpStatus.BAD_REQUEST);
            }
        } else {
            result.put("error_code", ErrorCode.getToken_expired());
            json = AppJsonUtils.returnFailJsonString(result, "非法token！");
            return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
        }


    }
}
