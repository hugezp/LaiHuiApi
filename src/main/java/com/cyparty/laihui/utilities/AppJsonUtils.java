package com.cyparty.laihui.utilities;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.*;
import com.google.gson.Gson;

import java.util.*;

/**
 * Created by zhu on 2016/10/21.
 */
public class AppJsonUtils {
    public static final String SLIDES = "slides";

    public static String returnFailJsonString(JSONObject result, String error_message) {
        JSONObject item = new JSONObject();
        item.put("message", error_message);
        item.put("status", false);
        item.put("result", result);
        String jsonString = JSON.toJSONString(item);
        return jsonString;
    }

    /**
     * 返回失败信息
     *
     * @param result  需要返回的值
     * @param message 提示信息
     * @return
     */
    public static String returnSuccessJsonString(JSONObject result, String message) {
        JSONObject item = new JSONObject();
        item.put("message", message);
        item.put("status", true);
        item.put("result", result);
        String jsonString = item.toJSONString();
        return jsonString;
    }

    //闪屏和弹出广告json
    public static JSONObject getCarouselJson(AppDB appDB) {
        JSONObject result_json = new JSONObject();
        int limit = 0;
        int flimit = 0;
        int start = 0;
        int fstart = 0;
        String where = " where pc_type = 2 order by pc_image_seq desc";
        String flashWhere = "where pc_type = 4 order by pc_image_seq desc";
        //弹出广告
        List<Carousel> carouselList = appDB.getCarousel(where);
        List<Carousel> splashScreenList = appDB.getCarousel(flashWhere);
        if (carouselList.size() == 0 && splashScreenList.size() == 0) {
            return result_json;
        }
        if (carouselList.size() > 0) {
            for (Carousel carousel : carouselList) {
                limit = limit + carousel.getSeq();
            }
            int rand = Utils.getRandomNum(limit);
            for (Carousel carousel : carouselList) {
                if (start <= rand && rand <= (carousel.getSeq() + start)) {
                    JSONObject carouselObject = new JSONObject();
                    carouselObject.put("image_url", carousel.getImage_url());
                    carouselObject.put("image_link", carousel.getImage_link());
                    carouselObject.put("image_title", carousel.getImage_title());
                    carouselObject.put("create_time", carousel.getCreate_time());
                    carouselObject.put("carousel_id", carousel.get_id());
                    result_json.put("slides", carouselObject);
                    break;
                }
                start = start + carousel.getSeq();
            }
        }
        if (splashScreenList.size() > 0) {
            for (Carousel flash : splashScreenList) {
                flimit = flimit + flash.getSeq();
            }
            int frand = Utils.getRandomNum(flimit);
            for (Carousel flash : splashScreenList) {
                if (fstart <= frand && frand <= (flash.getSeq() + fstart)) {
                    JSONObject flashObject = new JSONObject();
                    flashObject.put("image_url", flash.getImage_url());
                    flashObject.put("image_link", flash.getImage_link());
                    flashObject.put("image_title", flash.getImage_title());
                    flashObject.put("create_time", flash.getCreate_time());
                    flashObject.put("carousel_id", flash.get_id());
                    result_json.put("flash", flashObject);
                    break;
                }
                fstart = fstart + flash.getSeq();
            }
        }
        return result_json;
    }

    /**
     * 获取ContentTypeList
     *
     * @param appDB 数据库连接
     * @return
     */

    public static JSONObject getCarBrand(AppDB appDB) {
        JSONObject return_json = new JSONObject();
        String where = " group by car_brand_id order by car_font_letter ASC";
        List<CarTypeData> carTypeDataList = appDB.getCarTypeData(where);
        LinkedHashMap<String, List<CarTypeData>> carTypeMap = new LinkedHashMap<>();
        for (CarTypeData carTypeData : carTypeDataList) {
            if (carTypeMap.get(carTypeData.getCar_font_letter()) == null) {
                List<CarTypeData> tempList = new ArrayList<>();
                tempList.add(carTypeData);
                carTypeMap.put(carTypeData.getCar_font_letter(), tempList);
            } else {
                List<CarTypeData> tempList = carTypeMap.get(carTypeData.getCar_font_letter());
                tempList.add(carTypeData);
            }
        }
        for (String key : carTypeMap.keySet()) {
            List<CarTypeData> tempList = carTypeMap.get(key);
            JSONArray jsonArray = new JSONArray();
            for (CarTypeData carTypeData : tempList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", carTypeData.getName());
                jsonObject.put("id", carTypeData.getBrand_id());
                jsonObject.put("logo", carTypeData.getLogo());
                jsonArray.add(jsonObject);
            }
            return_json.put(key, jsonArray);
        }
        return return_json;
    }

    public static JSONObject getCarTypeBrand(AppDB appDB, String brand_id) {
        JSONObject return_json = new JSONObject();
        String where = " where car_brand_id =" + brand_id;
        List<CarTypeData> carTypeDataList = appDB.getCarTypeData(where);
        LinkedHashMap<String, List<CarTypeData>> carTypeMap = new LinkedHashMap<>();
        JSONArray jsonArray = new JSONArray();
        for (CarTypeData carTypeData : carTypeDataList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("brand_type_name", carTypeData.getBrand_type_name());
            jsonObject.put("brand_type_id", carTypeData.getBrand_type_id());
            jsonArray.add(jsonObject);
        }
        return_json.put("data", jsonArray);
        if (carTypeDataList.size() > 0) {
            return_json.put("name", carTypeDataList.get(0).getName());
            return_json.put("logo", carTypeDataList.get(0).getLogo());
        }
        return return_json;
    }

    public static JSONObject getlicensehead() {
        //String json="{\"1\":\"京\",\"3\":\"沪\",\"26\":\"粤\",\"15\":\"浙\",\"29\":\"川\",\"17\":\"鄂\",\"19\":\"甘\",\"16\":\"赣\",\"18\":\"桂\",\"25\":\"贵\",\"9\":\"黑\",\"23\":\"吉\",\"20\":\"晋\",\"2\":\"津\",\"8\":\"辽\",\"12\":\"鲁\",\"21\":\"蒙\",\"24\":\"闽\",\"30\":\"宁\",\"27\":\"青\",\"31\":\"琼\",\"22\":\"陕\",\"14\":\"苏\",\"11\":\"皖\",\"10\":\"湘\",\"13\":\"新\",\"5\":\"冀\",\"4\":\"渝\",\"6\":\"豫\",\"7\":\"云\",\"28\":\"藏\"}";
        String json = "{\'1\':\'京\',\'3\':\'沪\',\'26\':\'粤\',\'15\':\'浙\',\'29\':\'川\',\'17\':\'鄂\',\'19\':\'甘\',\'16\':\'赣\',\'18\':\'桂\',\'25\':\'贵\',\'9\':\'黑\',\'23\':\'吉\',\'20\':\'晋\',\'2\':\'津\',\'8\':\'辽\',\'12\':\'鲁\',\'21\':\'蒙\',\'24\':\'闽\',\'30\':\'宁\',\'27\':\'青\',\'31\':\'琼\',\'22\':\'陕\',\'14\':\'苏\',\'11\':\'皖\',\'10\':\'湘\',\'13\':\'新\',\'5\':\'冀\',\'4\':\'渝\',\'6\':\'豫\',\'7\':\'云\',\'28\':\'藏\'}";
        JSONObject return_json = JSONObject.parseObject(json);
        return return_json;
    }

    public static JSONObject getcolor() {
        String json = "{\"errno\":\"0\",\"errmsg\":\"\",\"data\":[{\"id\":\"1\",\"name\":\"\\u767d\\u8272\",\"value\":\"#ffffff\"},{\"id\":\"2\",\"name\":\"\\u7ea2\\u8272\",\"value\":\"#ff7878\"},{\"id\":\"3\",\"name\":\"\\u9ed1\\u8272\",\"value\":\"#7c7c7c\"},{\"id\":\"4\",\"name\":\"\\u94f6\\u8272\",\"value\":\"#cccccc\"},{\"id\":\"9\",\"name\":\"\\u91d1\\u8272\",\"value\":\"#fed02e\"},{\"id\":\"10\",\"name\":\"\\u7070\\u8272\",\"value\":\"#C3C3C5\"},{\"id\":\"5\",\"name\":\"\\u84dd\\u8272\",\"value\":\"#507cb7\"},{\"id\":\"6\",\"name\":\"\\u9ec4\\u8272\",\"value\":\"#feee34\"},{\"id\":\"7\",\"name\":\"\\u7eff\\u8272\",\"value\":\"#60a470\"},{\"id\":\"8\",\"name\":\"\\u5176\\u4ed6\",\"value\":\"#f7f7f7\"}]}";
        JSONObject return_json = JSONObject.parseObject(json);
        return return_json;
    }

    //用户基本信息
    public static JSONObject getUserInfo(AppDB appDB, int id) {
        JSONObject return_json = new JSONObject();
        JSONObject driver_json = new JSONObject();
        JSONObject passenger_json = new JSONObject();
        String where = " where _id=" + id;
        User user = new User();
        List<User> userList = appDB.getUserList(where);
        if (userList.size() > 0) {
            user = userList.get(0);
        }
        if (user.getUser_id() != 0) {
            int driver_status = user.getIs_car_owner();
            int passenger_status = user.getIs_validated();
            if (driver_status == 1) {
                where = " where user_id=" + id;
                List<CarOwnerInfo> carOwnerInfoList = appDB.getCarOwnerInfo(where);
                if (carOwnerInfoList.size() > 0) {
                    JSONObject info_json = new JSONObject();
                    CarOwnerInfo carOwnerInfo = carOwnerInfoList.get(0);
                    info_json.put("name", carOwnerInfo.getCar_owner_name());
                    info_json.put("idsn", carOwnerInfo.getIdsn());
                    if (user.getFlag() == 0) {
                        info_json.put("car_no", carOwnerInfo.getCar_id());
                        info_json.put("car_brand", carOwnerInfo.getCar_brand());
                        info_json.put("car_color", carOwnerInfo.getCar_color());
                        info_json.put("car_type", carOwnerInfo.getCar_type());
                    } else {
                        List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(user.getUser_id());
                        if (travelCardInfos.size() > 0) {
                            UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                            info_json.put("car_no", travelCardInfo.getCar_license_number());
                            info_json.put("car_brand", "");
                            info_json.put("car_color", travelCardInfo.getCar_color());
                            info_json.put("car_type", travelCardInfo.getCar_type());
                        }
                    }
                    driver_json.put("status", 1);
                    driver_json.put("info", info_json);
                } else {
                    driver_json.put("status", driver_status);
                }
            } else {
                driver_json.put("status", driver_status);
            }
            if (passenger_status == 1) {
                passenger_json.put("name", user.getUser_name());
                passenger_json.put("idsn", user.getUser_idsn());
                passenger_json.put("status", 1);
                passenger_json.put("flag", user.getU_flag());
            } else {
                passenger_json.put("status", 0);
            }
        }
        PCCount passengerPCCount = getPCCount(appDB, user.getUser_id());
        String mobile = user.getUser_mobile();
        return_json.put("pc_count", passengerPCCount.getTotal());
        return_json.put("passenger_validate", passenger_json);
        return_json.put("driver_validate", driver_json);
        return_json.put("avatar", user.getAvatar());
        return_json.put("mobile", mobile);
        boolean is_manager = false;
        List<String> managerList = new ArrayList<>();
        managerList.add("13838741275");
        managerList.add("13073733023");//周慧杰
        managerList.add("18338228688");//李博士
        managerList.add("15737121009");//孙桂军
        managerList.add("15136475852");//张庆
        managerList.add("15737894861");//杨硕
        if (managerList.contains(mobile)) {
            is_manager = true;
        }
        return_json.put("is_manager", is_manager);
        return return_json;
    }

    public static JSONObject getAPPDriverDepartureList(AppDB appDB, int page, int size, int departure_address_code, int destination_address_code, int user_id, double p_start_lat, double p_start_lng, double p_end_lat, double p_end_lng) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String where = " where is_enable=1 ";
        List<DepartureInfo> departureInfoList = new ArrayList<>();
        int offset = page * size;
        int count = 0;
        if (departure_address_code != 0 && destination_address_code != 0) {
            //1.最精确
            where = where + " and departure_address_code=" + departure_address_code + " and destination_address_code=" + destination_address_code + " and departure_time>='" + Utils.getCurrentTime() + "'" + " order by departure_time ASC limit " + offset + "," + size;
            List<DepartureInfo> departureInfoList1 = appDB.getAppDriverDpartureInfo(where);
            //2.锁定目的地code
            String city_code = (departure_address_code + "").substring(0, 4) + "00";
            where = " where is_enable=1 and departure_city_code =" + city_code + " and departure_address_code!=" + departure_address_code + " and destination_address_code=" + destination_address_code + " and departure_time>='" + Utils.getCurrentTime() + "'" + " order by departure_time ASC limit " + offset + "," + size;
            List<DepartureInfo> departureInfoList2 = appDB.getAppDriverDpartureInfo(where);
            //3.锁定出发地code
            String des_city_code = (destination_address_code + "").substring(0, 4) + "00";
            where = " where is_enable=1  and departure_address_code=" + departure_address_code + " and destination_address_code!=" + destination_address_code + " and destination_city_code = " + des_city_code + " and departure_time>='" + Utils.getCurrentTime() + "'" + " order by departure_time ASC limit " + offset + "," + size;
            List<DepartureInfo> departureInfoList3 = appDB.getAppDriverDpartureInfo(where);
            //4.两边都使用citycode
            where = " where is_enable=1 and departure_city_code =" + city_code + " and departure_address_code!=" + departure_address_code + " and destination_address_code !=" + destination_address_code + " and destination_city_code = " + des_city_code + " and departure_time>='" + Utils.getCurrentTime() + "'" + " order by departure_time ASC limit " + offset + "," + size;
            List<DepartureInfo> departureInfoList4 = appDB.getAppDriverDpartureInfo(where);
            departureInfoList.addAll(departureInfoList1);
            departureInfoList.addAll(departureInfoList2);
            departureInfoList.addAll(departureInfoList3);
            departureInfoList.addAll(departureInfoList4);
        } else {
            if (user_id != 0) {
                where = where + " and user_id=" + user_id;
            } else {
                where = where + " and departure_time>='" + Utils.getCurrentTime() + "'";
            }
            count = appDB.getCount("pc_driver_publish_info", where);
            //按照创建时间倒序排列
            where = where + " order by create_time DESC limit " + offset + "," + size;
            //where = where + " order by create_time DESC limit 0,50 ";
            departureInfoList = appDB.getAppDriverDpartureInfo(where);
        }
        //乘客路程距离
        double my_distance = RangeUtils.getDistance(p_start_lat, p_start_lng, p_end_lat, p_end_lng);
        for (DepartureInfo departure : departureInfoList) {
            String boarding_point = departure.getBoarding_point();
            String breakout_point = departure.getBreakout_point();
            //获取车主起点经纬度
            net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(boarding_point);
            double d_start_longitude = Double.parseDouble("".equals(jsonObject.get("longitude").toString()) ? "-256.18" : jsonObject.get("longitude").toString());
            double d_start_latitude = Double.parseDouble("".equals(jsonObject.get("latitude").toString()) ? "-256.18" : jsonObject.get("latitude").toString());
            //获取车主终点经纬度
            net.sf.json.JSONObject jsonObject1 = net.sf.json.JSONObject.fromObject(breakout_point);
            double d_end_longitude = Double.parseDouble("".equals(jsonObject1.get("longitude").toString()) ? "-256.18" : jsonObject1.get("longitude").toString());
            double d_end_latitude = Double.parseDouble("".equals(jsonObject1.get("latitude").toString()) ? "-256.18" : jsonObject1.get("latitude").toString());
            //计算车主与乘客起点的距离
            double start_distance = RangeUtils.getDistance(p_start_lat, p_start_lng, d_start_latitude, d_start_longitude);
            //计算车主与乘客终点的距离
            double end_distance = RangeUtils.getDistance(p_end_lat, p_end_lng, d_end_latitude, d_end_longitude);
            //计算匹配度
            String suitability = RangeUtils.getSuitability(my_distance, start_distance, end_distance);
            JSONObject driverObject = new JSONObject();
            JSONObject dataObject = new JSONObject();
            driverObject.put("suitability", suitability);
            driverObject.put("start_point_distance", start_distance);
            driverObject.put("end_point_distance", end_distance);
            driverObject.put("mobile", departure.getMobile());
            driverObject.put("source", departure.getSource());
            driverObject.put("price", departure.getPrice());
            where = " where user_id=" + departure.getUser_id();
            List<CarOwnerInfo> carOwnerInfoList = appDB.getCarOwnerInfo(where);
            List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(departure.getUser_id());
            if (carOwnerInfoList.size() > 0) {
                CarOwnerInfo carOwnerInfo = carOwnerInfoList.get(0);
                if (carOwnerInfo.getFlag() == 0) {
                    driverObject.put("car_no", carOwnerInfo.getCar_id());
                    driverObject.put("car_brand", carOwnerInfo.getCar_brand());
                    driverObject.put("car_color", carOwnerInfo.getCar_color());
                    driverObject.put("car_type", carOwnerInfo.getCar_type());
                }
                if (travelCardInfos.size() > 0) {
                    UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                    driverObject.put("car_no", travelCardInfo.getCar_license_number());
                    driverObject.put("car_brand", "");
                    driverObject.put("car_color", travelCardInfo.getCar_color());
                    driverObject.put("car_type", travelCardInfo.getCar_type());
                }
            } else {
                if (travelCardInfos.size() > 0) {
                    UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                    driverObject.put("car_no", travelCardInfo.getCar_license_number());
                    driverObject.put("car_brand", "");
                    driverObject.put("car_color", travelCardInfo.getCar_color());
                    driverObject.put("car_type", travelCardInfo.getCar_type());
                } else {
                    driverObject.put("car_no", "");
                    driverObject.put("car_brand", "");
                    driverObject.put("car_color", "");
                    driverObject.put("car_type", "");
                }
            }
            String name = departure.getUser_name();
            driverObject.put("name", name);
            driverObject.put("avatar", departure.getUser_avatar());
            PCCount driverPCCount = getPCCount(appDB, departure.getUser_id());
            driverObject.put("pc_count", driverPCCount.getTotal());
            //判断订单是否已过时
            long departure_time = Utils.date2TimeStamp(departure.getStart_time());
            long current_time = Utils.getCurrenTimeStamp();
            boolean is_available = false;
            if (departure_time > current_time) {
                is_available = true;
            }
            dataObject.put("id", departure.getR_id());
            dataObject.put("is_available", is_available);
            dataObject.put("departure_time", departure.getStart_time());
            dataObject.put("seats", departure.getInit_seats());
            //剩余座位
            dataObject.put("current_seats", departure.getCurrent_seats());
            dataObject.put("create_time", DateUtils.getTimesToNow(departure.getCreate_time()));
            dataObject.put("boarding_point", JSONObject.parseObject(departure.getBoarding_point()));
            dataObject.put("breakout_point", JSONObject.parseObject(departure.getBreakout_point()));
            dataObject.put("remark", departure.getRemark());
            dataObject.put("user_data", driverObject);
            dataArray.add(dataObject);
        }
        result_json.put("data", dataArray);
        result_json.put("total", count);
        result_json.put("page", page);
        result_json.put("size", size);
        return result_json;
    }

    public static JSONObject getAPPDriverDepartureInfo(AppDB appDB, String order_id, int user_id) {
        JSONObject jsonObject = new JSONObject();
        JSONObject return_json = new JSONObject();
        String where = " where is_enable=1 and a._id= " + order_id;
        List<DepartureInfo> departureInfoList = appDB.getAppDriverDpartureInfo(where);
        for (DepartureInfo departure : departureInfoList) {
            JSONObject driverObject = new JSONObject();
            jsonObject.put("car_id", departure.getR_id());
            where = " and user_id =" + departure.getUser_id();
            List<CarOwnerInfo> carOwnerInfoList = appDB.getCarOwnerInfo1(where);
            if (carOwnerInfoList.size() > 0) {
                CarOwnerInfo carOwnerInfo = carOwnerInfoList.get(0);
                if (carOwnerInfo.getFlag() == 0) {
                    driverObject.put("car_no", carOwnerInfo.getCar_id());
                    driverObject.put("car_brand", carOwnerInfo.getCar_brand());
                    driverObject.put("car_color", carOwnerInfo.getCar_color());
                    driverObject.put("car_type", carOwnerInfo.getCar_type());
                } else {
                    //车单品牌类型
                    UserTravelCardInfo travelCardInfo = appDB.getTravelCard(departure.getUser_id()).get(0);
                    //车牌号
                    driverObject.put("car_brand", "");
                    driverObject.put("car_no", travelCardInfo.getCar_license_number());
                    driverObject.put("car_color", travelCardInfo.getCar_color());
                    driverObject.put("car_type", travelCardInfo.getCar_type());
                }
            } else {
                //车单品牌类型
                List<UserTravelCardInfo> travelCardInfoList = appDB.getTravelCard(departure.getUser_id());
                if (travelCardInfoList.size() > 0) {
                    UserTravelCardInfo travelCardInfo = travelCardInfoList.get(0);
                    //车牌号
                    driverObject.put("car_brand", "");
                    driverObject.put("car_no", travelCardInfo.getCar_license_number());
                    driverObject.put("car_color", travelCardInfo.getCar_color());
                    driverObject.put("car_type", travelCardInfo.getCar_type());
                }
            }
            String name = departure.getUser_name();
            driverObject.put("name", name);
            driverObject.put("avatar", departure.getUser_avatar());
            driverObject.put("mobile", departure.getMobile());
            driverObject.put("user_id", departure.getUser_id());
            PCCount driverPCCount = getPCCount(appDB, departure.getUser_id());
            driverObject.put("pc_count", driverPCCount.getTotal());
            //判断订单是否已过时
            long departure_time = Utils.date2TimeStamp(departure.getStart_time());
            long current_time = Utils.getCurrenTimeStamp();
            boolean is_available = false;
            if (departure_time > current_time) {
                is_available = true;
            }
            //判断是否还可以看到乘客电话(用户可以在发车后一天之内看到电话)
            boolean mobile_available = false;
            String yester_time = Utils.getCurrentTimeSubOrAddHour(-24);
            long yester_timestamp = Utils.date2TimeStamp(yester_time);
            long departure_timestamp = Utils.date2TimeStamp(departure.getStart_time());

            if (departure_timestamp > yester_timestamp) {
                mobile_available = true;
            }
            jsonObject.put("driver_id", departure.getR_id());
            jsonObject.put("start_time", departure.getStart_time());
            jsonObject.put("boarding_point", JSONObject.parseObject(departure.getBoarding_point()));
            jsonObject.put("breakout_point", JSONObject.parseObject(departure.getBreakout_point()));
            jsonObject.put("seats", departure.getInit_seats());
            jsonObject.put("current_seats", departure.getCurrent_seats());
            jsonObject.put("remark", departure.getRemark());
            jsonObject.put("info_status", departure.getStatus());
            jsonObject.put("mobile_available", mobile_available);
            jsonObject.put("is_available", is_available);
            jsonObject.put("create_time", departure.getCreate_time());
            jsonObject.put("points", departure.getPoints());
            jsonObject.put("description", departure.getDescription());
            jsonObject.put("price", departure.getPrice());
            String order_where = " where order_id=" + departure.getR_id() + "  and is_enable=1 and order_type=1 ";//只查询订单类型为订单的
            int booking_count = appDB.getCount("pc_orders", order_where);
            jsonObject.put("booking_count", booking_count);
            if (user_id != 0) {
                order_where = order_where + " and  user_id=" + user_id;
            }
            double total_price = 0;
            List<Order> orderList = appDB.getOrderReview(order_where, 0);
            //获取乘客订单
            List<Order> orders = appDB.getOrderReview(" where user_id=" + user_id + " and order_type=0 and is_enable=1 order by create_time DESC limit 1", 0);
            if (orders.size() > 0) {
                Order order = orders.get(0);
                int newOrder_id = order.getOrder_id();
                List<Order> orderNewList = appDB.getOrderReview(" where order_id = " + newOrder_id + " and order_type =2 and is_enable=1 order by create_time DESC limit 1", 0);
                if (orderNewList.size() > 0) {
                    Order driverOrder = orderNewList.get(0);
                    int grab_id = driverOrder.get_id();
                    jsonObject.put("grab_id", grab_id);
                }
            }
            JSONArray orderArray = new JSONArray();
            if (orderList.size() > 0) {
                for (Order order : orderList) {
                    JSONObject passengerData = new JSONObject();
                    JSONObject orderObject = new JSONObject();
                    String passenger_order_where = " where _id=" + order.getOrder_id();
                    PassengerOrder passengerOrder = appDB.getPassengerDepartureInfo(passenger_order_where).get(0);
                    orderObject.put("order_id", order.get_id());
                    orderObject.put("order_status", order.getOrder_status());
                    orderObject.put("boarding_point", JSONObject.parseObject(passengerOrder.getBoarding_point()));
                    orderObject.put("breakout_point", JSONObject.parseObject(passengerOrder.getBreakout_point()));
                    orderObject.put("booking_seats", passengerOrder.getSeats());
                    orderObject.put("create_time", passengerOrder.getCreate_time());
                    orderObject.put("description", passengerOrder.getDescription());
                    passengerData.put("name", passengerOrder.getUser_name());
                    passengerData.put("mobile", passengerOrder.getMobile());
                    passengerData.put("avatar", passengerOrder.getUser_avatar());
                    PCCount passengerPCCount = getPCCount(appDB, passengerOrder.getUser_id());
                    passengerData.put("pc_count", passengerPCCount.getTotal());
                    orderObject.put("user_data", passengerData);
                    //司机已经确认的金额
                    if (order.getOrder_status() == 1) {
                        total_price = total_price + passengerOrder.getPay_money();
                    }
                    orderArray.add(orderObject);
                }
            }
            jsonObject.put("total_price", total_price);
            jsonObject.put("user_data", driverObject);

            return_json.put("driver_data", jsonObject);
            return_json.put("passenger_data", orderArray);
        }
        return return_json;
    }

    /**
     * 41匹配乘客模块
     */
    public static JSONObject getPassengerDepartureList(AppDB appDB, int page, int size, int departure_address_code, int destination_address_code, int id, double departure_lon, double departure_lat, double destinat_lon, double destinat_lat) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String where = " where is_enable=1   ";
        String forword_where = " where is_enable=1  and order_status!=1";
        List<PassengerOrder> passengerOrderList = new ArrayList<>();
        int count = 0;
        int offset = page * size;
        if (id == 0) {
            if (departure_address_code != 0 && destination_address_code != 0) {
                //1.最精确
                where = forword_where + "  and departure_address_code=" + departure_address_code + " and destination_address_code=" + destination_address_code + " and departure_time>='" + Utils.getCurrentTime() + "'" + " order by departure_time ASC limit " + offset + "," + size;
                List<PassengerOrder> passengerOrderList1 = appDB.getPassengerDepartureInfo(where);
                //2.锁定目的地code
                String city_code = (departure_address_code + "").substring(0, 4) + "00";
                where = forword_where + "  and departure_city_code =" + city_code + " and departure_address_code!=" + departure_address_code + " and destination_address_code=" + destination_address_code + " and departure_time>='" + Utils.getCurrentTime() + "'" + " order by departure_time ASC limit " + offset + "," + size;
                List<PassengerOrder> passengerOrderList2 = appDB.getPassengerDepartureInfo(where);
                //3.锁定出发地code
                String des_city_code = (destination_address_code + "").substring(0, 4) + "00";
                where = forword_where + "  and departure_address_code=" + departure_address_code + " and destination_address_code!=" + destination_address_code + " and destination_city_code = " + des_city_code + " and departure_time>='" + Utils.getCurrentTime() + "'" + " order by departure_time ASC limit " + offset + "," + size;
                List<PassengerOrder> passengerOrderList3 = appDB.getPassengerDepartureInfo(where);
                //4.两边都使用citycode
                where = forword_where + "  and  departure_city_code =" + city_code + " and departure_address_code!=" + departure_address_code + " and destination_address_code !=" + destination_address_code + " and destination_city_code = " + des_city_code + " and departure_time>='" + Utils.getCurrentTime() + "'" + " order by departure_time ASC limit " + offset + "," + size;
                List<PassengerOrder> passengerOrderList4 = appDB.getPassengerDepartureInfo(where);
                passengerOrderList.addAll(passengerOrderList1);
                passengerOrderList.addAll(passengerOrderList2);
                passengerOrderList.addAll(passengerOrderList3);
                passengerOrderList.addAll(passengerOrderList4);
            } else {
                //查询出发时间大于等于现在时间的乘客车单
                where = forword_where + " and departure_time>='" + Utils.getCurrentTime() + "'" + " order by departure_time ASC ";
                passengerOrderList = appDB.getPassengerDepartureInfo(where);
            }
        } else {
            //根据前端页面提供的id查询乘客车单
            where = " where  a.is_enable=1 and a._id=" + id;
            passengerOrderList = appDB.getPassengerDepartureInfo(where);
        }
        count = passengerOrderList.size();
        for (PassengerOrder departure : passengerOrderList) {
            JSONObject passengerObject = new JSONObject();
            JSONObject dataObject = new JSONObject();
            JSONObject driverObject = new JSONObject();
            String name = departure.getUser_name();
            if (departure.getSource() == 5) {
                passengerObject.put("mobile", departure.getPay_num());
            } else {
                passengerObject.put("mobile", departure.getMobile());
            }
            passengerObject.put("source", departure.getSource());
            passengerObject.put("name", name);
            passengerObject.put("avatar", departure.getUser_avatar());
            PCCount passengerPCCount = getPCCount(appDB, departure.getUser_id());
            passengerObject.put("pc_count", passengerPCCount.getTotal());
            String order_where = " a join pc_passenger_publish_info b on a.order_id = b._id where a.order_type=0 and a.order_id=" + departure.get_id();
            List<Order> orderList = appDB.getOrderReview(order_where, 2);
            Order order = new Order();
            if (orderList.size() > 0) {
                order = orderList.get(0);
                dataObject.put("id", orderList.get(0).get_id());
                dataObject.put("info_id", orderList.get(0).getOrder_id());
            } else {
                dataObject.put("info_id", departure.get_id());
            }
            dataObject.put("isArrive", order.getIsArrive());
            dataObject.put("order_status", order.getOrder_status());
            dataObject.put("is_enable", departure.getIs_enable());
            dataObject.put("departure_time", departure.getDeparture_time());
            dataObject.put("seats", departure.getSeats());
            dataObject.put("description", departure.getDescription());
            dataObject.put("price", departure.getPay_money());
            dataObject.put("create_time", DateUtils.getTimesToNow(departure.getCreate_time()));
            dataObject.put("boarding_point", JSONObject.parseObject(departure.getBoarding_point()));
            dataObject.put("breakout_point", JSONObject.parseObject(departure.getBreakout_point()));
            double o_departure_lon = Double.parseDouble((JSONObject.parseObject(departure.getBoarding_point()).get("longitude").toString()).equals("") ? "-256.18" : JSONObject.parseObject(departure.getBoarding_point()).get("longitude").toString());
            double o_departure_lat = Double.parseDouble((JSONObject.parseObject(departure.getBoarding_point()).get("latitude").toString()).equals("") ? "-256.18" : JSONObject.parseObject(departure.getBoarding_point()).get("latitude").toString());
            double o_destinat_lon = Double.parseDouble((JSONObject.parseObject(departure.getBoarding_point()).get("longitude").toString()).equals("") ? "-256.18" : JSONObject.parseObject(departure.getBreakout_point()).get("longitude").toString());
            double o_destinat_lat = Double.parseDouble((JSONObject.parseObject(departure.getBoarding_point()).get("latitude").toString()).equals("") ? "-256.18" : JSONObject.parseObject(departure.getBreakout_point()).get("latitude").toString());
            double my_distance = RangeUtils.getDistance(departure_lat, departure_lon, destinat_lat, destinat_lon);
            double start_point_distance = RangeUtils.getDistance(departure_lat, departure_lon, o_departure_lat, o_departure_lon);
            double end_point_distance = RangeUtils.getDistance(destinat_lat, destinat_lon, o_destinat_lat, o_destinat_lon);
            String suitability = RangeUtils.getSuitability(my_distance, start_point_distance, end_point_distance);
            dataObject.put("start_point_distance", start_point_distance);
            dataObject.put("end_point_distance", end_point_distance);
            dataObject.put("suitability", suitability);
            dataObject.put("remark", departure.getRemark());
            dataObject.put("user_data", passengerObject);
            dataArray.add(dataObject);
        }
        result_json.put("data", dataArray);
        result_json.put("total", count);
        result_json.put("page", page);
        result_json.put("size", size);
        return result_json;
    }

    public static PCCount getPCCount(AppDB appDB, int user_id) {
        PCCount pcCount = new PCCount();
        //统计司机发布全部拼车次数
        String where = " where user_id =" + user_id + " and is_enable=1";
        int driver_departure_total = appDB.getCount("pc_driver_publish_info", where);//司机发车次数

        String where_count = where + " and order_status =3 and order_type=2";
        int booking_total = appDB.getCount("pc_orders", where_count);//司机订单次数
        where_count = where + " and order_status =4 and order_type=0";
        int passenger_total = appDB.getCount("pc_orders", where_count);//订单次数

        pcCount.setDriver_departure_count(driver_departure_total);

        pcCount.setPassenger_booking_count(booking_total);


        pcCount.setTotal(driver_departure_total + booking_total + passenger_total);

        return pcCount;
    }

    /**
     * 乘客订单列表获取模块
     *
     * @param appDB
     * @param page    第几页
     * @param size    每页记录条数
     * @param user_id 乘客user_id
     * @return
     */
    public static JSONObject getMyBookingOrderList(AppDB appDB, int page, int size, int user_id) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        //获得该用户的所有可用的订单记录列表
        String where = " a right join pc_passenger_publish_info b on a.order_id=b._id where a.is_enable=1 and a.order_type=0 and a.user_id=" + user_id;
        int count = appDB.getCount("pc_orders", where);
        int offset = page * size;
        where = where + " order by a.create_time DESC limit " + offset + "," + size;
        List<Order> orderList = appDB.getOrderReview(where, 2);
        int status = 0;
        //状态备注
        String remark = "";
        int isArrive = 0;
        for (Order order : orderList) {
            JSONObject jsonObject = new JSONObject();
            JSONObject userObject = new JSONObject();
            //订单记录id
            jsonObject.put("order_id", order.get_id());
            //乘客车单id
            jsonObject.put("record_id", order.getOrder_id());
            jsonObject.put("update_time", order.getUpdate_time());
            jsonObject.put("order_status", order.getOrder_status());//order_status<=3
            jsonObject.put("seats", order.getBooking_seats());
            jsonObject.put("boarding_point", JSONObject.parseObject(order.getBoarding_point()));
            jsonObject.put("breakout_point", JSONObject.parseObject(order.getBreakout_point()));
            jsonObject.put("descriptions", order.getDescription());
            jsonObject.put("create_time", order.getCreate_time());
            jsonObject.put("departure_time", order.getDeparture_time());
            jsonObject.put("price", order.getPrice());
            jsonObject.put("remark", order.getRemark());
            //判断乘客的订单状态和备注
            switch (order.getOrder_status()) {
                case 0:
                    status = 1;
                    remark = "等待抢单";
                    break;
                case 1:
                    status = 1;
                    remark = "司机抢单";
                    break;
                case 2:
                    status = 1;
                    remark = "待支付";
                    break;
                case 3:
                    status = 2;
                    remark = "支付成功";
                    break;
                case 4:
                    status = 3;
                    remark = "订单完成";
                    break;
                case -1:
                    status = 3;
                    remark = "申请退款";
                    break;
                case 5:
                    status = 4;
                    remark = "订单关闭";
                    break;
                case 6:
                    status = 3;
                    remark = "退款成功";
                    break;
                case 100:
                    status = 1;
                    isArrive = 1;
                    remark = "司机抢单";
                    break;
                case 200:
                    status = 0;
                    isArrive = 1;
                    remark = "已支付,等待抢单";
                    break;

                case 300:
                    status = 2;
                    isArrive = 1;
                    remark = "等待发车";
                    break;
            }
            //乘客订单状态
            jsonObject.put("status", status);
            //乘客订单状态备注
            jsonObject.put("remake", remark);
            jsonObject.put("isArrive", isArrive);
            //得到乘客基本信息
            String passenger_where = " where _id=" + order.getUser_id();
            User passenger = appDB.getUserList(passenger_where).get(0);
            JSONObject passengerData = new JSONObject();
            passengerData.put("user_mobile", passenger.getUser_mobile());
            passengerData.put("user_avatar", passenger.getAvatar());
            passengerData.put("user_name", passenger.getUser_nick_name());
            //得到司机基本信息
            where = " where order_id=" + order.getOrder_id() + " and order_type=2 and order_status!=-2 and is_enable=1";//查询已经抢单且有效的司机抢单记录
            List<Order> driverOrders = appDB.getOrderReview(where, 0);
            for (Order order1 : driverOrders) {
                where = " where user_id=" + order1.getUser_id();
                List<CarOwnerInfo> carOwnerInfoList = appDB.getCarOwnerInfo(where);
                if (carOwnerInfoList.size() > 0) {
                    CarOwnerInfo carOwnerInfo = carOwnerInfoList.get(0);
                    if (carOwnerInfo.getFlag() == 0) {
                        userObject.put("car_no", carOwnerInfo.getCar_id());
                        userObject.put("car_brand", carOwnerInfo.getCar_brand());
                        userObject.put("car_color", carOwnerInfo.getCar_color());
                        userObject.put("car_type", carOwnerInfo.getCar_type());
                    } else {
                        List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(order1.getUser_id());
                        if (travelCardInfos.size() > 0) {
                            UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                            userObject.put("car_no", travelCardInfo.getCar_license_number());
                            userObject.put("car_brand", "");
                            userObject.put("car_color", travelCardInfo.getCar_color());
                            userObject.put("car_type", travelCardInfo.getCar_type());
                        }

                    }
                    userObject.put("grab_id", order1.get_id());
                    userObject.put("mobile", carOwnerInfo.getMobile());
                    userObject.put("name", carOwnerInfo.getUser_name());
                    userObject.put("avatar", carOwnerInfo.getUser_avatar());
                    PCCount driverPCCount = getPCCount(appDB, carOwnerInfo.getUser_id());
                    userObject.put("pc_count", driverPCCount.getTotal());
                } else {
                    List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(order1.getUser_id());
                    if (travelCardInfos.size() > 0) {
                        UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                        userObject.put("car_no", travelCardInfo.getCar_license_number());
                        userObject.put("car_brand", "");
                        userObject.put("car_color", travelCardInfo.getCar_color());
                        userObject.put("car_type", travelCardInfo.getCar_type());
                    }
                    where = " where _id=" + order1.getUser_id();
                    User user = appDB.getUserList(where).get(0);
                    userObject.put("grab_id", order1.get_id());
                    userObject.put("mobile", user.getUser_mobile());
                    userObject.put("name", user.getUser_name());
                    userObject.put("avatar", user.getAvatar());
                    PCCount driverPCCount = getPCCount(appDB, user.getUser_id());
                    userObject.put("pc_count", driverPCCount.getTotal());
                }


            }
            jsonObject.put("driver_data", userObject);
            jsonObject.put("passenger_data", passengerData);

            dataArray.add(jsonObject);
        }
        result_json.put("data", dataArray);
        result_json.put("total", count);
        result_json.put("page", page);
        result_json.put("size", size);
        return result_json;
    }

    /**
     * 推送信息获取
     *
     * @param appDB
     * @param order
     * @param role  角色 1：乘客 2：车主
     * @return
     */
    public static Map getPushObject(AppDB appDB, Order order, int role) {
        Map userObject = new HashMap();
        //得到乘客基本信息
        if (role == 1) {
            String passenger_where = " where _id=" + order.getUser_id();
            User passenger = appDB.getUserList(passenger_where).get(0);
            Map passengerData = new HashMap();
            passengerData.put("order_status", 100);
            passengerData.put("user_mobile", passenger.getUser_mobile());
            passengerData.put("user_avatar", passenger.getAvatar());
            passengerData.put("user_name", passenger.getUser_nick_name());
            PCCount driverPCCount = getPCCount(appDB, passenger.getUser_id());
            passengerData.put("pc_count", driverPCCount.getTotal());
            return passengerData;
        }
        if (role == 2) {
            //得到司机基本信息
            String where = " where order_id=" + order.getOrder_id() + " and order_type=2 and order_status=0 and is_enable=1";//查询已经抢单且有效的司机抢单记录
            List<Order> driverOrders = appDB.getOrderReview(where, 0);
            for (Order order1 : driverOrders) {
                where = " where user_id=" + order1.getUser_id();
                List<CarOwnerInfo> carOwnerInfoList = appDB.getCarOwnerInfo(where);
                if (carOwnerInfoList.size() > 0) {
                    CarOwnerInfo carOwnerInfo = carOwnerInfoList.get(0);
                    userObject.put("order_status", 100);
                    if (carOwnerInfo.getFlag() == 0) {
                        userObject.put("car_no", carOwnerInfo.getCar_id());
                        userObject.put("car_brand", carOwnerInfo.getCar_brand());
                        userObject.put("car_color", carOwnerInfo.getCar_color());
                        userObject.put("car_type", carOwnerInfo.getCar_type());
                    } else {
                        UserTravelCardInfo travelCardInfo = appDB.getTravelCard(order1.getUser_id()).get(0);
                        userObject.put("car_no", travelCardInfo.getCar_license_number());
                        userObject.put("car_brand", "");
                        userObject.put("car_color", travelCardInfo.getCar_color());
                        userObject.put("car_type", travelCardInfo.getCar_type());
                    }
                    userObject.put("grab_id", order1.get_id());
                    userObject.put("mobile", carOwnerInfo.getMobile());
                    userObject.put("name", carOwnerInfo.getUser_name());
                    userObject.put("avatar", carOwnerInfo.getUser_avatar());
                    PCCount driverPCCount = getPCCount(appDB, carOwnerInfo.getUser_id());
                    userObject.put("pc_count", driverPCCount.getTotal());
                }
            }
            return userObject;
        } else {
            return null;
        }
    }

    public static JSONObject getMyBookingOrderInfo(AppDB appDB, int order_id) {
        JSONObject jsonObject = new JSONObject();
        String where = " a right join pc_passenger_publish_info b on a.order_id=b._id where a.is_enable=1 and a._id=" + order_id;
        List<Order> orderList = appDB.getOrderReview(where, 2);
        for (Order order : orderList) {
            JSONObject userObject = new JSONObject();
            //乘客订单记录id
            jsonObject.put("order_id", order.get_id());
            //乘客车单id
            jsonObject.put("record_id", order.getOrder_id());
            String update_time = Utils.getTimeSubOrAdd(order.getUpdate_time(), 15);
            jsonObject.put("update_time", update_time);
            jsonObject.put("order_status", order.getOrder_status());//order_status<=3
            jsonObject.put("seats", order.getBooking_seats());
            jsonObject.put("boarding_point", JSONObject.parseObject(order.getBoarding_point()));
            jsonObject.put("breakout_point", JSONObject.parseObject(order.getBreakout_point()));
            jsonObject.put("description", order.getDescription());
            jsonObject.put("create_time", order.getCreate_time());
            jsonObject.put("departure_time", order.getDeparture_time());
            //jsonObject.put("price", order.getPrice());
            jsonObject.put("remark", order.getRemark());
            //得到乘客基本信息
            String passenger_where = " where _id=" + order.getUser_id();
            User passenger = appDB.getUserList(passenger_where).get(0);
            JSONObject passengerData = new JSONObject();
            passengerData.put("user_mobile", passenger.getUser_mobile());
            passengerData.put("user_avatar", passenger.getAvatar());
            passengerData.put("user_name", passenger.getUser_nick_name());

            //得到司机基本信息
            where = " where order_id=" + order.getOrder_id() + " and order_type=2 and order_status!=4 and is_enable=1";//查询已经抢单且有效的司机抢单记录

            List<Order> driverOrders = appDB.getOrderReview(where, 0);
            for (Order order1 : driverOrders) {
                where = " where user_id=" + order1.getUser_id();
                List<CarOwnerInfo> carOwnerInfoList = appDB.getCarOwnerInfo(where);
                where = " where user_id = " + order1.getUser_id() + " and is_enable=1 order by CONVERT (create_time USING gbk)COLLATE gbk_chinese_ci desc limit 1";
                List<DepartureInfo> departureInfoList = appDB.getAppDriverDpartureInfo(where);
                DepartureInfo departureInfo = departureInfoList.get(0);
                userObject.put("driver_id", departureInfo.getR_id());
                userObject.put("ini_seats", departureInfo.getInit_seats());
                userObject.put("current_seats", departureInfo.getCurrent_seats());
                jsonObject.put("price", order.getPrice());
                if (carOwnerInfoList.size() > 0) {
                    CarOwnerInfo carOwnerInfo = carOwnerInfoList.get(0);
                    if (carOwnerInfo.getFlag() == 0) {
                        userObject.put("car_no", carOwnerInfo.getCar_id());
                        userObject.put("car_brand", carOwnerInfo.getCar_brand());
                        userObject.put("car_color", carOwnerInfo.getCar_color());
                        userObject.put("car_type", carOwnerInfo.getCar_type());
                    } else {
                        UserTravelCardInfo travelCardInfo = appDB.getTravelCard(order1.getUser_id()).get(0);
                        userObject.put("car_no", travelCardInfo.getCar_license_number());
                        userObject.put("car_brand", "");
                        userObject.put("car_color", travelCardInfo.getCar_color());
                        userObject.put("car_type", travelCardInfo.getCar_type());
                    }
                    userObject.put("grab_id", order1.get_id());
                    userObject.put("mobile", carOwnerInfo.getMobile());
                    userObject.put("name", carOwnerInfo.getUser_name());
                    userObject.put("avatar", carOwnerInfo.getUser_avatar());
                    PCCount driverPCCount = getPCCount(appDB, carOwnerInfo.getUser_id());
                    userObject.put("pc_count", driverPCCount.getTotal());
                } else {
                    List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(order1.getUser_id());
                    if (travelCardInfos.size() > 0) {
                        UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                        userObject.put("car_no", travelCardInfo.getCar_license_number());
                        userObject.put("car_brand", "");
                        userObject.put("car_color", travelCardInfo.getCar_color());
                        userObject.put("car_type", travelCardInfo.getCar_type());
                    }
                    where = " where _id = " + order1.getUser_id();
                    User user = appDB.getUserList(where).get(0);
                    userObject.put("grab_id", order1.get_id());
                    userObject.put("mobile", user.getUser_mobile());
                    userObject.put("name", user.getUser_name());
                    userObject.put("avatar", user.getAvatar());
                    PCCount driverPCCount = getPCCount(appDB, user.getUser_id());
                    userObject.put("pc_count", driverPCCount.getTotal());
                }
            }
            jsonObject.put("driver_data", userObject);
            jsonObject.put("passenger_data", passengerData);
        }

        return jsonObject;
    }

    public static JSONObject getMyArriveBookingOrderInfo(AppDB appDB, int order_id) {
        JSONObject result = new JSONObject();
        JSONObject orderResult = new JSONObject();
        String where = " a right join pc_passenger_publish_info b on a.order_id=b._id where a.is_enable=1 and a._id=" + order_id;
        List<Order> orderList = appDB.getOrderReview(where, 2);
        for (Order order : orderList) {
            JSONObject driverResult = new JSONObject();
            //乘客订单记录id
            orderResult.put("order_id", order.get_id());
            //乘客车单id
            orderResult.put("record_id", order.getOrder_id());
            String update_time = Utils.getTimeSubOrAdd(order.getUpdate_time(), 15);
            orderResult.put("update_time", update_time);
            orderResult.put("order_status", order.getOrder_status());//order_status<=3
            orderResult.put("seats", order.getBooking_seats());
            orderResult.put("boarding_point", JSONObject.parseObject(order.getBoarding_point()));
            orderResult.put("breakout_point", JSONObject.parseObject(order.getBreakout_point()));
            orderResult.put("description", order.getDescription());
            orderResult.put("create_time", order.getCreate_time());
            orderResult.put("departure_time", order.getDeparture_time());
            //result.put("price", order.getPrice());
            orderResult.put("remark", order.getRemark());
            //得到司机基本信息
            where = " where order_id=" + order.getOrder_id() + " and order_type=2 and order_status!=4 and is_enable=1";//查询已经抢单且有效的司机抢单记录
            List<Order> driverOrders = appDB.getOrderReview(where, 0);
            for (Order order1 : driverOrders) {
                where = " where _id = " + order1.getUser_id();
                User user = appDB.getUserList(where).get(0);
                driverResult.put("grab_id", order1.get_id());
                driverResult.put("name", user.getUser_name());
                driverResult.put("mobile", user.getUser_mobile());
                driverResult.put("avatar", user.getAvatar());
                where = " where user_id=" + order1.getUser_id();
                List<CarOwnerInfo> carOwnerInfoList = appDB.getCarOwnerInfo(where);
                orderResult.put("price", order.getPrice());
                if (user.getIs_car_owner() == 1) {
                    if (carOwnerInfoList.size() > 0) {
                        CarOwnerInfo carOwnerInfo = carOwnerInfoList.get(0);
                        if (carOwnerInfo.getFlag() == 0) {
                            driverResult.put("car_no", carOwnerInfo.getCar_id());
                            driverResult.put("car_brand", carOwnerInfo.getCar_brand());
                            driverResult.put("car_color", carOwnerInfo.getCar_color());
                            driverResult.put("car_type", carOwnerInfo.getCar_type());
                        } else {
                            UserTravelCardInfo travelCardInfo = appDB.getTravelCard(order1.getUser_id()).get(0);
                            driverResult.put("car_no", travelCardInfo.getCar_license_number());
                            driverResult.put("car_brand", "");
                            driverResult.put("car_color", travelCardInfo.getCar_color());
                            driverResult.put("car_type", travelCardInfo.getCar_type());
                        }
                    } else {
                        List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(order1.getUser_id());
                        if (travelCardInfos.size() > 0) {
                            UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                            driverResult.put("car_no", travelCardInfo.getCar_license_number());
                            driverResult.put("car_brand", "");
                            driverResult.put("car_color", travelCardInfo.getCar_color());
                            driverResult.put("car_type", travelCardInfo.getCar_type());
                        } else {
                            driverResult.put("car_no", "");
                            driverResult.put("car_brand", "");
                            driverResult.put("car_color", "");
                            driverResult.put("car_type", "");
                        }
                    }
                } else {
                    driverResult.put("car_no", "");
                    driverResult.put("car_brand", "");
                    driverResult.put("car_color", "");
                    driverResult.put("car_type", "");
                }
            }
            result.put("driver_data", driverResult);
            result.put("order_data", orderResult);
        }

        return result;
    }

    //司机获取抢单列表
    public static JSONObject getMyGrabOrderList(AppDB appDB, int page, int size, int user_id) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String where = " a right join pc_passenger_publish_info b on a.order_id=b._id where a.is_enable=1 and a.order_type=2 and a.user_id=" + user_id;
        int count = appDB.getCount("pc_orders", where);
        int offset = page * size;
        int status = 0;
        String remark = "";
        int isArrive = 0;
        where = where + " order by a.create_time DESC limit " + offset + "," + size;
        List<Order> orderList = appDB.getOrderReview(where, 2);
        for (Order order : orderList) {
            JSONObject jsonObject = new JSONObject();
            JSONObject userObject = new JSONObject();
            JSONObject driverObject = new JSONObject();
            jsonObject.put("order_id", order.get_id());
            jsonObject.put("order_status", order.getOrder_status());
            jsonObject.put("update_time", order.getUpdate_time());
            jsonObject.put("seats", order.getBooking_seats());
            jsonObject.put("boarding_point", JSONObject.parseObject(order.getBoarding_point()));
            jsonObject.put("breakout_point", JSONObject.parseObject(order.getBreakout_point()));
            jsonObject.put("description", order.getDescription());
            jsonObject.put("create_time", order.getCreate_time());
            jsonObject.put("departure_time", order.getDeparture_time());
            jsonObject.put("price", order.getPrice());
            jsonObject.put("remark", order.getRemark());
            //判断车主的订单状态和备注
            switch (order.getOrder_status()) {
                case 0:
                    status = 1;
                    remark = "等待确认";
                    break;
                case 1:
                    status = 1;
                    remark = "抢单成功";
                    break;
                case 2:
                    status = 1;
                    remark = "乘客支付成功";
                    break;
                case 3:
                    status = 3;
                    remark = "订单完成";
                    break;
                case 4:
                    status = 3;
                    remark = "抢单失败";
                    break;
                case 5:
                    status = 4;
                    remark = "乘客订单失效";
                    break;
                case 6:
                    status = 1;
                    remark = "乘客申请退款";
                    break;
                case -1:
                    status = 2;
                    remark = "已发车";
                    break;
                case 100:
                    status = 1;
                    remark = "抢单成功,待乘客确认";
                    break;
                case 200:
                    status = 1;
                    remark = "乘客支付成功";
                    break;
            }
            //乘客订单状态
            jsonObject.put("status", status);
            //乘客订单状态备注
            jsonObject.put("remake", remark);
            jsonObject.put("isArrive", order.getIsArrive());

            //得到乘客基本信息
            where = " where _id=" + order.getUser_id();
            List<User> passengers = appDB.getUserList(where);
            if (passengers.size() > 0) {
                User user = passengers.get(0);
                userObject.put("mobile", user.getUser_mobile());
                userObject.put("name", user.getUser_nick_name());
                userObject.put("avatar", user.getAvatar());

                PCCount driverPCCount = getPCCount(appDB, user.getUser_id());
                userObject.put("pc_count", driverPCCount.getTotal());
            }
            //得到司机基本信息
            where = " where _id=" + order.getDriver_id();
            User driver = appDB.getUserList(where).get(0);

            driverObject.put("mobile", driver.getUser_mobile());
            driverObject.put("name", driver.getUser_nick_name());
            driverObject.put("avatar", driver.getAvatar());
            driverObject.put("isVerification", driver.getIs_car_owner());

            PCCount driverPCCount = getPCCount(appDB, driver.getUser_id());
            driverObject.put("pc_count", driverPCCount.getTotal());

            jsonObject.put("passenger_data", userObject);
            jsonObject.put("driver_data", driverObject);

            dataArray.add(jsonObject);

        }
        result_json.put("data", dataArray);
        result_json.put("total", count);
        result_json.put("page", page);
        result_json.put("size", size);
        return result_json;
    }

    public static JSONObject getMyGrabOrderList(AppDB appDB, int user_id) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String where = " a right join pc_passenger_publish_info b on a.order_id=b._id where a.order_status in(-1,0,1,2,100,200) and a.is_enable=1 and a.order_type=2 and a.user_id=" + user_id;
        int count = appDB.getCount("pc_orders", where);
        int status = 0;
        String remark = "";
        where = where + " order by a.create_time DESC";
        List<Order> orderList = appDB.getOrderReview(where, 2);
        for (Order order : orderList) {
            JSONObject jsonObject = new JSONObject();
            JSONObject userObject = new JSONObject();
            JSONObject driverObject = new JSONObject();
            jsonObject.put("order_id", order.get_id());
            jsonObject.put("order_status", order.getOrder_status());
            jsonObject.put("update_time", order.getUpdate_time());
            jsonObject.put("seats", order.getBooking_seats());
            jsonObject.put("boarding_point", JSONObject.parseObject(order.getBoarding_point()));
            jsonObject.put("breakout_point", JSONObject.parseObject(order.getBreakout_point()));
            jsonObject.put("description", order.getDescription());
            jsonObject.put("create_time", order.getCreate_time());
            jsonObject.put("departure_time", order.getDeparture_time());
            jsonObject.put("price", order.getPrice());
            jsonObject.put("remark", order.getRemark());
            //判断车主的订单状态和备注
            switch (order.getOrder_status()) {
                case 0:
                    status = 1;
                    remark = "等待确认";
                    break;
                case 1:
                    status = 1;
                    remark = "抢单成功";
                    break;
                case 2:
                    status = 1;
                    remark = "乘客支付成功";
                    break;
                case 3:
                    status = 3;
                    remark = "订单完成";
                    break;
                case 4:
                    status = 3;
                    remark = "抢单失败";
                    break;
                case 5:
                    status = 4;
                    remark = "乘客订单失效";
                    break;
                case 6:
                    status = 1;
                    remark = "乘客申请退款";
                    break;
                case -1:
                    status = 2;
                    remark = "已发车";
                    break;
                case 100:
                    status = 1;
                    remark = "抢单成功,待乘客确认";
                    break;
                case 200:
                    status = 1;
                    remark = "乘客支付成功";
                    break;
            }
            //乘客订单状态
            jsonObject.put("status", status);
            //乘客订单状态备注
            jsonObject.put("remake", remark);
            jsonObject.put("isArrive", order.getIsArrive());

            //得到乘客基本信息
            where = " where _id=" + order.getUser_id();
            List<User> passengers = appDB.getUserList(where);
            if (passengers.size() > 0) {
                User user = passengers.get(0);
                userObject.put("mobile", user.getUser_mobile());
                userObject.put("name", user.getUser_nick_name());
                userObject.put("avatar", user.getAvatar());

                PCCount driverPCCount = getPCCount(appDB, user.getUser_id());
                userObject.put("pc_count", driverPCCount.getTotal());
            }
            //得到司机基本信息
            where = " where _id=" + order.getDriver_id();
            User driver = appDB.getUserList(where).get(0);

            driverObject.put("mobile", driver.getUser_mobile());
            driverObject.put("name", driver.getUser_nick_name());
            driverObject.put("avatar", driver.getAvatar());
            driverObject.put("isVerification", driver.getIs_car_owner());

            PCCount driverPCCount = getPCCount(appDB, driver.getUser_id());
            driverObject.put("pc_count", driverPCCount.getTotal());

            jsonObject.put("passenger_data", userObject);
            jsonObject.put("driver_data", driverObject);

            dataArray.add(jsonObject);

        }
        result_json.put("data", dataArray);
        return result_json;
    }

    /**
     * 获取订单状态
     *
     * @param appDB
     * @param page    第几页
     * @param size    每页记录条数
     * @param user_id 用户id
     * @return
     */
    public static JSONObject getMyPassengerStatusList(AppDB appDB, int page, int size, int user_id) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String where = " a right join pc_passenger_publish_info b on a.order_id=b._id where a.is_enable=1  and a.order_type=0   and (a.order_status<4 or a.order_status in(100,200,300)) and a.order_id in(select order_id from pc_orders where user_id=" + user_id + " and order_type=2 and is_enable=1)";
        int offset = page * size;
        int status = 0;
        String remake = "";
        int isArrive = 0;
        where = where + " order by a.create_time DESC limit " + offset + "," + size;
        List<Order> orderList = appDB.getOrderReview(where, 2);
        for (Order order : orderList) {
            JSONObject jsonObject = new JSONObject();
            JSONObject userObject = new JSONObject();
            //订单记录id
            jsonObject.put("order_id", order.get_id());
            jsonObject.put("order_status", order.getOrder_status());
            jsonObject.put("update_time", order.getUpdate_time());
            jsonObject.put("order_type", order.getOrder_type());
            jsonObject.put("remark", order.getRemark());
            jsonObject.put("seats", order.getBooking_seats());
            jsonObject.put("boarding_point", JSONObject.parseObject(order.getBoarding_point()));
            jsonObject.put("breakout_point", JSONObject.parseObject(order.getBreakout_point()));
            jsonObject.put("description", order.getDescription());
            jsonObject.put("create_time", order.getCreate_time());
            jsonObject.put("departure_time", order.getDeparture_time());
            jsonObject.put("price", order.getPrice());
            jsonObject.put("isArrive", order.getIsArrive());
            //从订单具体状态判断车单进行的状态
            //乘客车单状态
            switch (order.getOrder_status()) {
                case 0:
                    status = 0;
                    remake = "等待抢单";
                    break;
                case 1:
                    status = 11;
                    remake = "司机抢单";
                    break;
                case 2:
                    status = 12;
                    remake = "确认待支付";
                    break;
                case 3:
                    status = 2;
                    remake = "支付成功";
                    break;
                case 4:
                    status = 3;
                    remake = "交易完成";
                    break;
                case -1:
                    status = 5;
                    remake = "申请退款";
                    break;
                case 100:
                    status = 11;
                    isArrive = 1;
                    remake = "司机抢单";
                    break;
                case 200:
                    status = 0;
                    isArrive = 1;
                    remake = "已支付,等待抢单";
                    break;

                case 300:
                    status = 2;
                    isArrive = 1;
                    remake = "等待发车";
                    break;
            }
            //车单状态
            jsonObject.put("status", status);
            //车单状态备注
            jsonObject.put("remake", remake);
            jsonObject.put("isArrive", isArrive);
            //得到乘客基本信息
            where = " where _id=" + order.getUser_id();
            List<User> passengers = appDB.getUserList(where);
            if (passengers.size() > 0) {
                User user = passengers.get(0);
                userObject.put("mobile", user.getUser_mobile());
                userObject.put("name", user.getUser_nick_name());
                userObject.put("avatar", user.getAvatar());
                PCCount driverPCCount = getPCCount(appDB, user.getUser_id());
                userObject.put("pc_count", driverPCCount.getTotal());
            }
            jsonObject.put("passenger_data", userObject);
            dataArray.add(jsonObject);

        }
        result_json.put("data", dataArray);
        return result_json;
    }

    public static JSONObject getMyDriverStatusList(AppDB appDB, int page, int size, int user_id, int order_id) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String where = " a right join pc_passenger_publish_info b on a.order_id=b._id where a.is_enable=1 and a.order_type=2 and a.order_id in (SELECT order_id from pc_orders where  order_type=0  and _id=" + order_id + " and user_id=" + user_id + ")";
        int offset = page * size;
        int status = 0;
        String remake = "";
        int isArrive = 0;
        where = where + " order by a.create_time DESC limit 1 ";
        List<Order> orderList = appDB.getOrderReview(where, 2);
        for (Order order : orderList) {
            JSONObject jsonObject = new JSONObject();
            JSONObject driverObject = new JSONObject();
            //从订单具体状态判断车单进行的状态
            //车主车单状态
            switch (order.getOrder_status()) {
                case 0:
                    status = 11;
                    remake = "等待确认";
                    break;
                case 1:
                    status = 12;
                    remake = "抢单成功";
                    break;
                case 2:
                    status = 2;
                    remake = "乘客支付成功";
                    break;
                case 3:
                    status = 6;
                    remake = "订单完成";
                    break;
                case 4:
                    status = 31;
                    remake = "乘客拒绝";
                    break;
                case 5:
                    status = 32;
                    remake = "车单失效";
                    break;
                case 6:
                    status = 7;
                    remake = "乘客申请退款";
                    break;
                case -1:
                    status = 5;
                    remake = "已发车";
                    break;
                case 100:
                    isArrive = 1;
                    status = 5;
                    remake = "抢单成功,等待乘客确认";
                    break;
                case 200:
                    isArrive = 1;
                    status = 5;
                    remake = "乘客已支付";
            }
            //车单状态
            jsonObject.put("status", status);
            jsonObject.put("isArrive", isArrive);
            //车单状态备注
            jsonObject.put("remake", remake);
            //得到司机基本信息
            List<DepartureInfo> departures = appDB.getAppDriverDpartureInfo("where a.is_enable=1 and a.user_id=" + order.getDriver_id() + " order by CAST(a.create_time AS time) DESC limit 1");
            if (departures.size() > 0) {
                if (departures.get(0).getCurrent_seats() == 0) {
                    //车单状态
                    jsonObject.put("status", "4");
                    //车单状态备注
                    jsonObject.put("remake", "该车辆已经没有座位，请换乘其他车辆");
                }
                DepartureInfo departure = departures.get(0);
                jsonObject.put("boarding_point", JSONObject.parseObject(departure.getBoarding_point()));
                jsonObject.put("breakout_point", JSONObject.parseObject(departure.getBreakout_point()));
                jsonObject.put("description", departure.getDescription());
                jsonObject.put("create_time", departure.getCreate_time());
                jsonObject.put("departure_time", departure.getStart_time());
                jsonObject.put("price", departure.getPrice());
                jsonObject.put("current_seats", departures.get(0).getCurrent_seats());
                jsonObject.put("init_seats", departures.get(0).getInit_seats());
                jsonObject.put("car_id", departures.get(0).getR_id());
                List<CarOwnerInfo> carOwnerInfos = appDB.getCarOwnerInfo("where a.user_id=" + order.getDriver_id());
                if (carOwnerInfos.size() > 0) {
                    CarOwnerInfo user = carOwnerInfos.get(0);
                    if (user.getFlag() == 0) {
                        driverObject.put("car", user.getCar_id());
                        driverObject.put("car_brand", user.getCar_brand());
                        driverObject.put("car_color", user.getCar_color());
                        driverObject.put("car_type", user.getCar_type());
                    } else {
                        List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(order.getDriver_id());
                        UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                        driverObject.put("car", travelCardInfo.getCar_license_number());
                        if (travelCardInfos.size() > 0) {
                            driverObject.put("car_brand", "");
                            driverObject.put("car_color", travelCardInfo.getCar_color());
                            driverObject.put("car_type", travelCardInfo.getCar_type());
                        }
                    }
                    driverObject.put("car_owner", user.getCar_owner());
                } else {
                    List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(order.getDriver_id());
                    UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                    driverObject.put("car", travelCardInfo.getCar_license_number());
                    if (travelCardInfos.size() > 0) {
                        driverObject.put("car_brand", "");
                        driverObject.put("car_color", travelCardInfo.getCar_color());
                        driverObject.put("car_type", travelCardInfo.getCar_type());
                    }
                }
                where = " where _id=" + order.getDriver_id();
                List<User> drivers = appDB.getUserList(where);
                if (drivers.size() > 0) {
                    User driver = drivers.get(0);
                    driverObject.put("mobile", driver.getUser_mobile());
                    driverObject.put("name", driver.getUser_nick_name());
                    driverObject.put("avatar", driver.getAvatar());

                    PCCount driverPCCount = getPCCount(appDB, driver.getUser_id());
                    driverObject.put("pc_count", driverPCCount.getTotal());
                }

            }
            jsonObject.put("driver_data", driverObject);
            dataArray.add(jsonObject);
        }
        result_json.put("data", dataArray);
        return result_json;
    }

    public static JSONObject getMyGrabOrderList(AppDB appDB, int page, int size, int user_id, int flag) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String where = " a right join pc_passenger_publish_info b on a.order_id=b._id where a.is_enable=1 and a.order_type=2 and a.user_id=" + user_id;
        if (flag == 1) {
            //查询历史
            where = where + " and a.order_status>=3";
        } else {
            where = where + " and a.order_status<3";
        }
        int count = appDB.getCount("pc_orders", where);
        int offset = page * size;

        where = where + " order by a.create_time DESC limit " + offset + "," + size;
        List<Order> orderList = appDB.getOrderReview(where, 2);
        for (Order order : orderList) {
            JSONObject jsonObject = new JSONObject();
            JSONObject userObject = new JSONObject();
            JSONObject driverObject = new JSONObject();

            jsonObject.put("order_id", order.get_id());
            jsonObject.put("order_status", order.getOrder_status());
            jsonObject.put("update_time", order.getUpdate_time());

            jsonObject.put("seats", order.getBooking_seats());
            jsonObject.put("boarding_point", JSONObject.parseObject(order.getBoarding_point()));
            jsonObject.put("breakout_point", JSONObject.parseObject(order.getBreakout_point()));
            jsonObject.put("description", order.getDescription());
            jsonObject.put("create_time", order.getCreate_time());
            jsonObject.put("departure_time", order.getDeparture_time());
            jsonObject.put("price", order.getPrice());
            //得到乘客基本信息
            where = " where _id=" + order.getUser_id();
            List<User> passengers = appDB.getUserList(where);
            if (passengers.size() > 0) {
                User user = passengers.get(0);
                userObject.put("mobile", user.getUser_mobile());
                userObject.put("name", user.getUser_nick_name());
                userObject.put("avatar", user.getAvatar());

                PCCount driverPCCount = getPCCount(appDB, user.getUser_id());
                userObject.put("pc_count", driverPCCount.getTotal());
            }
            //得到司机基本信息
            where = " where _id=" + order.getDriver_id();
            User driver = appDB.getUserList(where).get(0);

            driverObject.put("mobile", driver.getUser_mobile());
            driverObject.put("name", driver.getUser_nick_name());
            driverObject.put("avatar", driver.getAvatar());

            PCCount driverPCCount = getPCCount(appDB, driver.getUser_id());
            driverObject.put("pc_count", driverPCCount.getTotal());

            jsonObject.put("passenger_data", userObject);
            jsonObject.put("driver_data", driverObject);

            dataArray.add(jsonObject);

        }
        result_json.put("data", dataArray);
        result_json.put("total", count);
        result_json.put("page", page);
        result_json.put("size", size);
        return result_json;
    }

    public static JSONObject getMyGrabOrderInfo(AppDB appDB, int order_id) {
        JSONObject jsonObject = new JSONObject();

        String where = " a right join pc_passenger_publish_info b on a.order_id=b._id where a.is_enable=1  and a._id=" + order_id;

        List<Order> orderList = appDB.getOrderReview(where, 2);
        for (Order order : orderList) {

            JSONObject userObject = new JSONObject();
            JSONObject driverObject = new JSONObject();

            jsonObject.put("order_id", order.get_id());
            jsonObject.put("order_status", order.getOrder_status());
            jsonObject.put("update_time", order.getUpdate_time());

            jsonObject.put("seats", order.getBooking_seats());
            jsonObject.put("boarding_point", JSONObject.parseObject(order.getBoarding_point()));
            jsonObject.put("breakout_point", JSONObject.parseObject(order.getBreakout_point()));
            jsonObject.put("description", order.getDescription());
            jsonObject.put("create_time", order.getCreate_time());
            jsonObject.put("departure_time", order.getDeparture_time());
            jsonObject.put("price", order.getPrice());
            jsonObject.put("remark", order.getRemark());
            //得到乘客基本信息
            where = " where _id=" + order.getUser_id();
            List<User> passengers = appDB.getUserList(where);
            if (passengers.size() > 0) {
                User user = passengers.get(0);
                userObject.put("mobile", user.getUser_mobile());
                userObject.put("name", user.getUser_nick_name());
                userObject.put("avatar", user.getAvatar());

                PCCount driverPCCount = getPCCount(appDB, user.getUser_id());
                userObject.put("pc_count", driverPCCount.getTotal());
            }
            //得到司机基本信息
            where = " where _id=" + order.getDriver_id();
            User driver = appDB.getUserList(where).get(0);
            driverObject.put("mobile", driver.getUser_mobile());
            driverObject.put("name", driver.getUser_nick_name());
            driverObject.put("avatar", driver.getAvatar());
            PCCount driverPCCount = getPCCount(appDB, driver.getUser_id());
            driverObject.put("pc_count", driverPCCount.getTotal());
            jsonObject.put("passenger_data", userObject);
            jsonObject.put("driver_data", driverObject);

        }
        return jsonObject;
    }

    public static JSONObject getApkUpdated(AppDB appDB, int source) {

        String where = " where source=" + source;
        List<ApkUpdate> apkUpdates = appDB.getApkUpdated(where);
        JSONObject return_json = new JSONObject();
        if (apkUpdates.size() > 0) {
            ApkUpdate apkUpdate = apkUpdates.get(0);
            return_json.put("url", apkUpdate.getDownload_url());
            return_json.put("is_must", apkUpdate.getIs_must());
            return_json.put("versionCode", apkUpdate.getVersionCode());
            return_json.put("source", apkUpdate.getSoucre());
            return_json.put("updateMessage", apkUpdate.getUpdateMessage());
        }
        return return_json;
    }

    //得到轮播图json
    public static JSONObject getCarouselJson(AppDB appDB, int page, int size, int id) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String where = " where pc_type=1 ";
        int offset = page * size;
        int count = 1;
        where = where + " order by pc_image_seq ASC ";
        if (id == 0) {
            count = appDB.getCarousel(where).size();
            where = where + " limit " + offset + "," + size;
        } else {
            where = " where _id=" + id;
        }
        List<Carousel> carouselList = appDB.getCarousel(where);
        for (Carousel carousel : carouselList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", carousel.get_id());

            jsonObject.put("image_seq", carousel.getSeq());
            jsonObject.put("image_url", carousel.getImage_url());
            jsonObject.put("image_link", carousel.getImage_link());
            jsonObject.put("image_title", carousel.getImage_title());
            jsonObject.put("image_subtitle", carousel.getImage_subtitle());
            jsonObject.put("create_time", carousel.getCreate_time());
            dataArray.add(jsonObject);
        }
        result_json.put("total", count);
        result_json.put("page", page);
        result_json.put("size", size);
        result_json.put(SLIDES, dataArray);
        return result_json;
    }

    //得到合作方json
    public static JSONObject getPartner(AppDB appDB) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String where = " where is_enable = 1";
        int count = appDB.getCount("pc_partner", where);
        List<Partner> partnerList = appDB.getPartnerList(where);
        for (Partner partner : partnerList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("partner_icon", partner.getPartnerIcon());
            jsonObject.put("partner_icon_url", partner.getPartnerIconUrl());
            jsonObject.put("partner_url", partner.getPartnerUrl());
            dataArray.add(jsonObject);
        }
        result_json.put("data", dataArray);
        result_json.put("count", count);
        return result_json;
    }

    //新闻列表查询数据
    public static JSONObject getNews(AppDB appDB, String where) {
        JSONArray dataArray = new JSONArray();
        JSONObject result_json = new JSONObject();
        List<News> newsList = appDB.getNewsList(where);
        for (News news : newsList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("news_id", news.getId());
            jsonObject.put("title", news.getTitle());
            jsonObject.put("description", news.getDescription());
            jsonObject.put("content", news.getContent());
            jsonObject.put("create_time", news.getCreateTime());
            jsonObject.put("update_time", news.getUpdateTime());
            jsonObject.put("publisher", news.getPublisher());
            dataArray.add(jsonObject);
        }
        int count = appDB.getCount("pc_news", where);
        result_json.put("data", dataArray);
        result_json.put("count", count);
        return result_json;
    }

    //得到轮播图json
    public static JSONObject getPopUpAdJson(AppDB appDB, int type) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String where = " where pc_type=2 order by pc_image_seq DESC";
        int limit = 0;
        List<Carousel> carouselList = appDB.getCarousel(where);
        if (type == 1) {
            //列表
            for (Carousel carousel : carouselList) {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("image_url", carousel.getImage_url());
                jsonObject.put("image_link", carousel.getImage_link());
                jsonObject.put("image_title", carousel.getImage_title());
                jsonObject.put("create_time", carousel.getCreate_time());
                jsonObject.put("carousel_id", carousel.get_id());

                dataArray.add(jsonObject);
            }
            result_json.put("data", dataArray);
            return result_json;
        }
        for (Carousel carousel : carouselList) {
            limit = limit + carousel.getSeq();
        }
        int rand = Utils.getRandomNum(limit);
        int start = 0;
        for (Carousel carousel : carouselList) {
            if (start <= rand && rand <= (carousel.getSeq() + start)) {
                result_json.put("image_url", carousel.getImage_url());
                result_json.put("image_link", carousel.getImage_link());
                result_json.put("image_title", carousel.getImage_title());
                result_json.put("create_time", carousel.getCreate_time());
                result_json.put("carousel_id", carousel.get_id());
                break;
            }
            start = start + carousel.getSeq();
        }

        return result_json;
    }

    //得到轮播图json
    public static JSONObject getNavigationJson(AppDB appDB) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        String where = " where pc_type=3 ";

        List<Carousel> carouselList = appDB.getCarousel(where);
        for (Carousel carousel : carouselList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", carousel.get_id());

            jsonObject.put("image_seq", carousel.getSeq());
            jsonObject.put("image_url", carousel.getImage_url());
            jsonObject.put("image_link", carousel.getImage_link());
            jsonObject.put("image_title", carousel.getImage_title());
            jsonObject.put("create_time", carousel.getCreate_time());
            dataArray.add(jsonObject);
        }
        result_json.put("slides", dataArray);
        return result_json;
    }


    //获取附近车主列表
    public static JSONObject getNearByOwnerList(List<DriverAndCar> nearByOwenrList, int page, int size, int count, AppDB appDB) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        if (nearByOwenrList.size() > 0) {
            for (DriverAndCar departureInfo : nearByOwenrList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("car_id", departureInfo.getR_id());
                jsonObject.put("mobile", departureInfo.getMobile());
                jsonObject.put("departure_time", DateUtils.getProcessdTime(departureInfo.getStart_time()));
                jsonObject.put("create_time", DateUtils.getTimesToNow(departureInfo.getCreate_time()));
                jsonObject.put("i_province", net.sf.json.JSONObject.fromObject(departureInfo.getBoarding_point()).get("province"));
                //出发城市
                jsonObject.put("i_city", net.sf.json.JSONObject.fromObject(departureInfo.getBoarding_point()).get("city"));
                String id = net.sf.json.JSONObject.fromObject(departureInfo.getBoarding_point()).get("id").toString();
                if (id == null) {
                    jsonObject.put("is_mobile_user", "");
                } else {
                    jsonObject.put("is_mobile_user", id);
                }
                //出发地点
                jsonObject.put("i_name", net.sf.json.JSONObject.fromObject(departureInfo.getBoarding_point()).get("name"));
                jsonObject.put("o_province", net.sf.json.JSONObject.fromObject(departureInfo.getBreakout_point()).get("province"));
                jsonObject.put("o_city", net.sf.json.JSONObject.fromObject(departureInfo.getBreakout_point()).get("city"));
                jsonObject.put("o_name", net.sf.json.JSONObject.fromObject(departureInfo.getBreakout_point()).get("name"));
                jsonObject.put("ini_seats", departureInfo.getInit_seats());
                jsonObject.put("current_seats", departureInfo.getCurrent_seats());
                jsonObject.put("price", departureInfo.getPrice());
                jsonObject.put("suitability", departureInfo.getSuitability());
                jsonObject.put("start_point_distance", departureInfo.getStart_point_distance());
                jsonObject.put("end_point_distance", departureInfo.getEnd_point_distance());
                if (departureInfo.getFlag() == 0) {
                    jsonObject.put("car_color", departureInfo.getCar_color());
                    jsonObject.put("car_type", departureInfo.getCar_type());
                } else {
                    //车辆品牌类型

                    List<UserTravelCardInfo> travelCardInfos = appDB.getTravelCard(departureInfo.getUser_id());
                    if (travelCardInfos.size() > 0) {
                        UserTravelCardInfo travelCardInfo = travelCardInfos.get(0);
                        travelCardInfo.getCar_license_number();
                        jsonObject.put("car_color", travelCardInfo.getCar_color());
                        jsonObject.put("car_type", travelCardInfo.getCar_type());
                    }
                    //车牌号
                }
                jsonObject.put("name", departureInfo.getUser_name());
                jsonObject.put("user_avatar", departureInfo.getUser_avatar());
                jsonObject.put("remark", departureInfo.getRemark());
                dataArray.add(jsonObject);
            }
        }
        result_json.put("owner_data", dataArray);
        result_json.put("count", count);
        result_json.put("page", page);
        result_json.put("size", size);
        return result_json;
    }

    //附近乘客
    public static JSONObject getNearByPassengerList(List<PassengerOrder> nearByPassengerList, int page, int size, int count) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();

        if (nearByPassengerList.size() > 0) {
            for (PassengerOrder passenger : nearByPassengerList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("car_id", passenger.get_id());
                String mobile = passenger.getMobile();
                if (mobile.equals("") || mobile == null) {
                    jsonObject.put("mobile", passenger.getPay_num());
                } else {
                    jsonObject.put("mobile", mobile);
                }
                jsonObject.put("departure_time", DateUtils.getProcessdTime(passenger.getDeparture_time()));
                jsonObject.put("create_time", DateUtils.getTimesToNow(passenger.getCreate_time()));
                jsonObject.put("price", passenger.getPay_money());
                jsonObject.put("suitability", passenger.getSuitability());
                jsonObject.put("start_point_distance", passenger.getStart_point_distance());
                jsonObject.put("end_point_distance", passenger.getEnd_point_distance());
                jsonObject.put("boarding_point", passenger.getBoarding_point());
                jsonObject.put("breakout_point", passenger.getBreakout_point());
                jsonObject.put("i_province", net.sf.json.JSONObject.fromObject(passenger.getBoarding_point()).get("province"));
                //出发城市
                jsonObject.put("i_city", net.sf.json.JSONObject.fromObject(passenger.getBoarding_point()).get("city"));
                //出发地点
                String id = net.sf.json.JSONObject.fromObject(passenger.getBoarding_point()).get("id").toString();
                if (id == null) {
                    jsonObject.put("is_mobile_user", "");
                } else {
                    jsonObject.put("is_mobile_user", id);
                }

                jsonObject.put("i_name", net.sf.json.JSONObject.fromObject(passenger.getBoarding_point()).get("name"));
                jsonObject.put("o_province", net.sf.json.JSONObject.fromObject(passenger.getBreakout_point()).get("province"));
                jsonObject.put("o_city", net.sf.json.JSONObject.fromObject(passenger.getBreakout_point()).get("city"));
                jsonObject.put("o_name", net.sf.json.JSONObject.fromObject(passenger.getBreakout_point()).get("name"));
                if (passenger.getUser_name() == null)
                    jsonObject.put("name", "");
                else
                    jsonObject.put("name", passenger.getUser_name());
                jsonObject.put("isArrive", passenger.getIsArrive());
                jsonObject.put("user_avatar", passenger.getUser_avatar());
                jsonObject.put("user_avatar", passenger.getUser_avatar());
                jsonObject.put("booking_seats", passenger.getSeats());
                jsonObject.put("remark", passenger.getRemark());
                dataArray.add(jsonObject);
            }
        }
        result_json.put("passenger_data", dataArray);
        result_json.put("page", page);
        result_json.put("count", count);
        result_json.put("size", size);
        return result_json;
    }


    /**
     * 待处理账单
     *
     * @param appDB
     * @param user_id  该用户id
     * @param flag
     * @param judgment 判断条件  passenger：查询目标为乘客，否则为车主
     * @return 乘客的待处理订单信息或车主的车单信息
     */
    public static JSONObject order(AppDB appDB, int user_id, int flag, String judgment) {
        JSONObject json_result = new JSONObject();
        JSONArray array = new JSONArray();
        int status = 0;
        String remake = "";
        int isArrive = 0;
        //判断为乘客乘客
        if (judgment.equals("passenger")) {
            String where = " a right join pc_passenger_publish_info b on a.order_id=b._id where  b.is_enable=1 and (a.order_status<3 or a.order_status in(100,200)) and a.order_status != -1 and a.user_id=" + user_id + " and order_type=0 order by a.order_status desc limit 0,1";
            List<Order> orderList = appDB.getOrderReview(where, 2);
            if (orderList.size() > 0) {
                for (Order order : orderList) {
                    JSONObject jsonObject = new JSONObject();
                    //乘客的订单id
                    jsonObject.put("order_id", order.get_id());
                    //乘客的车单ID
                    jsonObject.put("record_id", order.getOrder_id());
                    //出发地
                    jsonObject.put("p_departure", order.getBoarding_point());
                    //目的地
                    jsonObject.put("p_destinat", order.getBreakout_point());
                    //几人同行
                    jsonObject.put("seats", order.getBooking_seats());
                    //价格
                    jsonObject.put("price", order.getPrice());
                    //备注
                    jsonObject.put("remark", order.getRemark());
                    //出发时间
                    jsonObject.put("p_departure_time", order.getDeparture_time());
                    switch (order.getOrder_status()) {
                        case 1:
                            status = 1;
                            remake = "司机抢单";
                            break;
                        case 2:
                            status = 1;
                            remake = "待支付";
                            break;
                        case -1:
                            status = 1;
                            remake = "退款申请";
                            break;
                        case 100:
                            status = 1;
                            isArrive = 1;
                            remake = "司机抢单";
                            break;
                        case 200:
                            status = 1;
                            isArrive = 1;
                            remake = "等待抢单";
                            break;
                        case 300:
                            status = 1;
                            isArrive = 1;
                            remake = "等待发车";
                            break;
                        case 0:
                            status = 1;
                            remake = "等待处理";
                    }
                    //分属状态值
                    jsonObject.put("status", status);
                    //分属状态
                    jsonObject.put("remake", remake);
                    jsonObject.put("isArrive", isArrive);
                    array.add(jsonObject);
                }
            }
        } else {
            String where = "where a.user_id =" + user_id + " and a.is_enable=1 order by CONVERT (create_time USING gbk)COLLATE gbk_chinese_ci desc limit 1";
            //车主的发布车单
            List<DepartureInfo> departureInfoList = appDB.getAppDriverDpartureInfo(where);
            if (departureInfoList.size() > 0) {
                for (DepartureInfo departureInfo : departureInfoList) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", departureInfo.getR_id());
                    jsonObject.put("current_seats", departureInfo.getCurrent_seats());
                    jsonObject.put("mobile", departureInfo.getMobile());
                    jsonObject.put("price", departureInfo.getPrice());
                    jsonObject.put("remark", departureInfo.getRemark());
                    jsonObject.put("create_time", DateUtils.getTimesToNow(departureInfo.getCreate_time()));
                    jsonObject.put("boarding_point", JSONObject.parseObject(departureInfo.getBoarding_point()));
                    jsonObject.put("breakout_point", JSONObject.parseObject(departureInfo.getBreakout_point()));
                    jsonObject.put("seats", departureInfo.getInit_seats());
                    jsonObject.put("departure_time", departureInfo.getStart_time());
                    array.add(jsonObject);
                }
            }
        }
        json_result.put("result_order", array);
        return json_result;
    }


    /**
     * 首页活动
     *
     * @return 封装的活动信息
     */
    public static JSONObject active(AppDB appDB) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("active", "邀好友，得奖励");
        jsonObject.put("url", "https://h5.laihuipinche.com/share_spread");
        return jsonObject;
    }

    /**
     * 首页活动图标数据
     *
     * @return 封装的活动信息
     */
    public static JSONObject activeIcon(AppDB appDB) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("active", "来回拼车");
        jsonObject.put("url", "https://h5.laihuipinche.com/app/pinche_FQA");
        return jsonObject;
    }


    /**
     * 获取常用路线
     *
     * @param appDB
     * @param user_id 用户i的
     * @param where   查询条件
     * @return 用户的常用路线信息
     */

    public static JSONObject commonRoute(AppDB appDB, int user_id, String where) {
        if (where == null) {
            where = " where user_id=" + user_id + " and is_enable=1  order by is_default desc";
        }
        List<CommonRoute> commonRouteList = appDB.getCommonRoute(where);
        JSONObject json_result = new JSONObject();
        JSONArray array = new JSONArray();
        if (commonRouteList.size() > 0) {
            for (CommonRoute commonRoute : commonRouteList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", commonRoute.getId());
                jsonObject.put("departure_city", commonRoute.getDeparture_city());
                jsonObject.put("departure_address", commonRoute.getDeparture_address());
                jsonObject.put("departure_lon", commonRoute.getDeparture_lon());
                jsonObject.put("departure_lat", commonRoute.getDeparture_lat());
                jsonObject.put("destinat_city", commonRoute.getDestinat_city());
                jsonObject.put("destinat_address", commonRoute.getDestinat_address());
                jsonObject.put("destinat_lon", commonRoute.getDestinat_lon());
                jsonObject.put("destinat_lat", commonRoute.getDestinat_lat());
                jsonObject.put("is_switch", commonRoute.getIs_switch());
                jsonObject.put("is_default", commonRoute.getIs_default());
                array.add(jsonObject);
            }
        }
        json_result.put("route", array);
        return json_result;
    }

    /**
     * 是否有新消息
     *
     * @param appDB
     * @param user_id 该用户的id
     * @return 是否有新消息 1：有 0：没有
     */
    public static JSONObject isNewMessage(AppDB appDB, int user_id) {
        List<PushNotification> pushList = appDB.getPushList("where receive_id=" + user_id + " and status=1 and is_enable=1");
        List<PushNotification> pushs = appDB.getPushList("where flag =1 and status=1 and is_enable=1");
        //消息条数
        int totalCount = pushList.size() + pushs.size();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalCount",totalCount);
        if (pushList.size() > 0 || pushs.size() > 0) {
            jsonObject.put("is_message", 1);
        } else {
            jsonObject.put("is_message", 0);
        }
        return jsonObject;
    }

    /**
     * 获得个人资料
     *
     * @param user
     */
    public static JSONObject getPersonalInfo(AppDB appDB, User user) {
        List<Popularize> populars = appDB.getPopular(user.getUser_id());
        JSONObject personal_data = new JSONObject();
        String nullString = "";
        int level = 1;
        if (populars.size() > 0) {
            Popularize popularize = populars.get(0);
            if (popularize.getLevel() == 0) {
                //专业代理员级别 0级最高 5级最低
                level = 0;
            }
        }
        personal_data.put("level", level);
        if (user.getAvatar().length() > 0 && !user.getAvatar().equals("null")) {
            personal_data.put("user_avater", user.getAvatar());
        } else {
            personal_data.put("user_avater", nullString);
        }
        if (user.getUser_name().length() > 0 && !user.getUser_name().equals("null")) {
            personal_data.put("user_name", user.getUser_name());
        } else {
            personal_data.put("user_name", nullString);
        }

        personal_data.put("is_validated", user.getIs_validated());

        if (user.getSex().length() > 0 && !user.getSex().equals("null")) {
            personal_data.put("user_sex", user.getSex());
        } else {
            personal_data.put("user_sex", nullString);
        }
        if (user.getSignature().length() > 0 && !user.getSignature().equals("null")) {
            personal_data.put("user_signature", user.getSignature());
        } else {
            personal_data.put("user_signature", "你还没有个人签名~");
        }
        if (user.getBirthday().length() > 0 && !user.getBirthday().equals("1000-01-01 00:00:00")) {
            String user_birthday = user.getBirthday().split(" ")[0];
            personal_data.put("user_birthday", user_birthday);
        } else {
            personal_data.put("user_birthday", nullString);
        }
        if (user.getHome().length() > 0 && !user.getHome().equals("null")) {
            personal_data.put("user_home", user.getHome());
        } else {
            personal_data.put("user_home", nullString);
        }
        if (user.getLive_city().length() > 0 && !user.getLive_city().equals("null")) {
            personal_data.put("user_live_city", user.getLive_city());
        } else {
            personal_data.put("user_live_city", nullString);
        }
        if (user.getCompany().length() > 0 && !user.getCompany().equals("null")) {
            personal_data.put("user_company", user.getCompany());
        } else {
            personal_data.put("user_company", nullString);
        }
        if (user.getDelivery_address().length() > 0 && !user.getDelivery_address().equals("null")) {
            personal_data.put("deliveryAddress", user.getDelivery_address());
        } else {
            personal_data.put("deliveryAddress", nullString);
        }
        return personal_data;
    }

    //获取系统消息或者活动消息的方法
    public static JSONObject getPushList(List<PushNotification> pushList, AppDB appDB, int flag, String msg) {
        JSONObject result = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (PushNotification push : pushList) {
            JSONObject pushJson = new JSONObject();
            pushJson.put("title", push.getTitle());
            pushJson.put("alert", push.getAlert());
            pushJson.put("type", push.getType());
            pushJson.put("time", push.getTime());
            pushJson.put("link_url", push.getLink_url());
            pushJson.put("imageUrl", push.getImageUrl());
            jsonArray.add(pushJson);
        }
        List<PushNotification> pushs = appDB.getPushList(" where flag=" + flag + " and is_enable=1 and status =1");
        if (pushs.size() > 0) {
            for (PushNotification push : pushs) {
                appDB.update("pc_push_notification", " set status = 0 where _id =" + push.get_id());
            }
        }
        result.put(msg, jsonArray);
        return result;
    }

    //车单推送消息返回结果
    public static JSONObject getPushOrder(List<PushNotification> pushList, AppDB appDB) {
        JSONObject result = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (PushNotification push : pushList) {
            JSONObject pushJson = new JSONObject();
            JSONObject infoJson = new JSONObject();
            pushJson.put("message_id", push.get_id());
            pushJson.put("order_id", push.getOrder_id());
            pushJson.put("push_id", push.getPush_id());
            pushJson.put("receive_id", push.getReceive_id());
            pushJson.put("push_type", push.getPush_type());
            pushJson.put("alert", push.getAlert());
            pushJson.put("type", push.getType());
            pushJson.put("isArrive", push.getIsArrive());
            pushJson.put("time", push.getTime());
            pushJson.put("status", push.getStatus());
            String where = " a join pc_passenger_publish_info b on a.order_id = b._id where a.order_type = 0 and a.is_enable = 1 and a.order_id = " + push.getOrder_id();
            List<Order> orderReview = appDB.getOrderReview(where, 2);
            if (orderReview.size() == 0)
                continue;
            Order order = orderReview.get(0);
            infoJson.put("departure_time", order.getDeparture_time());
            infoJson.put("order_id", String.valueOf(order.getOrder_id()));
            infoJson.put("price", String.valueOf(order.getPrice()));
            infoJson.put("seats", String.valueOf(order.getBooking_seats()));
            pushJson.put("order_status", String.valueOf(order.getOrder_status()));
            infoJson.put("boarding_point", order.getBoarding_point());
            infoJson.put("breakout_point", order.getBreakout_point());
            infoJson.put("record_id", String.valueOf(order.getOrder_id()));
            infoJson.put("departure_time", "");
            pushJson.put("info", infoJson);
            List<User> users = appDB.getUserList(" where _id =" + push.getPush_id());
            if (users.size() > 0) {
                pushJson.put("avatar", users.get(0).getAvatar());
                pushJson.put("user_name", users.get(0).getUser_nick_name());
            } else {
                pushJson.put("avatar", "");
                pushJson.put("user_name", "");
            }
            jsonArray.add(pushJson);
        }
        result.put("order_msg", jsonArray);
        return result;
    }

    //车单推送系统消息需要接受者id
    public static JSONObject getPushAll(AppDB appDB, int flag, String title, int id) {
        JSONObject result = new JSONObject();
        int total = 0;
        String where = " where flag = " + flag + " and is_enable=1 and receive_id =" + id + " order by time desc limit 1 ";
        List<PushNotification> pushActivity = appDB.getPushList(where);
        if (pushActivity.size() > 0) {
            total = appDB.getTotalCount("pc_push_notification", " where flag = " + flag + " and is_enable=1 and receive_id =" + id + " and status = 1 ");
            PushNotification push = pushActivity.get(0);
            result.put("title", title);
            result.put("content", push.getAlert());
            result.put("time", push.getTime());
            result.put("total", total);
            where = " a join pc_passenger_publish_info b on a.order_id = b._id where a.order_type = 2 and a.is_enable = 1 and a.order_id = " + push.getOrder_id();
            List<Order> orderReview = appDB.getOrderReview(where, 2);
            result.put("info", orderReview);
        } else {
            result.put("title", title);
            result.put("content", "");
            result.put("time", "");
            result.put("total", total);
            result.put("info", "");
        }
        return result;
    }

    //精选活动不需要接收者id,
    public static JSONObject getPushActivity(AppDB appDB, int flag, String title) {
        JSONObject result = new JSONObject();
        int total = 0;
        String where = " where flag = " + flag + " and is_enable=1 order by time desc limit 1 ";
        List<PushNotification> pushActivity = appDB.getPushList(where);
        if (pushActivity.size() > 0) {
            total = appDB.getTotalCount("pc_push_notification", " where flag = " + flag + " and is_enable=1 and status = 1");
            PushNotification push = pushActivity.get(0);
            result.put("title", title);
            result.put("content", push.getAlert());
            result.put("time", push.getTime());
            result.put("total", total);
        } else {
            result.put("title", title);
            result.put("content", "");
            result.put("time", "");
            result.put("total", total);
        }
        return result;
    }


}
