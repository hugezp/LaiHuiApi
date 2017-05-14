package com.cyparty.laihui.domain;

/**
 * Created by YG on 2017/5/4.
 */
public class CrossCity {
    private int _id;
    private String departure_city;//出发城市
    private String destination_city;//目的城市
    private String departure;//出发地
    private String destination;//目的地
    private String end_time;//结束时间
    private int departure_address_code;//出发地址代码
    private int destination_address_code;//目的地代码
    private int departure_city_code;//出发城市代码
    private int destination_city_code;//目的城市代码
    private int booking_seats;//预定座位
    private int r_id;
    private int user_id;
    private double price;
    //是否可用
    private int is_enable;
    //状态
    private int status;
    //出发时间 车辆型号 车辆颜色
    private String start_time, car_type, car_color;
    //始发地
    private String boarding_point;
    //目的地
    private String breakout_point;
    //车辆座位数
    private int init_seats;
    //车辆剩余座位数
    private int current_seats;
    //创建时间
    private String create_time;

    private String points;
    //描述
    private String description;

    //手机号
    //起点县
    String departure_county;

    private String mobile;
    //车主名
    String driving_name;
    //车主用户名
    String user_name;
    //车主头像
    String user_avatar;
    private String remark;
    private int flag;

    public int getDeparture_city_code() {
        return departure_city_code;
    }

    public void setDeparture_city_code(int departure_city_code) {
        this.departure_city_code = departure_city_code;
    }

    public int getDestination_city_code() {
        return destination_city_code;
    }

    public void setDestination_city_code(int destination_city_code) {
        this.destination_city_code = destination_city_code;
    }

    public int getR_id() {
        return r_id;
    }

    public void setR_id(int r_id) {
        this.r_id = r_id;
    }

    public int getIs_enable() {
        return is_enable;
    }

    public void setIs_enable(int is_enable) {
        this.is_enable = is_enable;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getCar_color() {
        return car_color;
    }

    public void setCar_color(String car_color) {
        this.car_color = car_color;
    }

    public int getInit_seats() {
        return init_seats;
    }

    public void setInit_seats(int init_seats) {
        this.init_seats = init_seats;
    }

    public int getCurrent_seats() {
        return current_seats;
    }

    public void setCurrent_seats(int current_seats) {
        this.current_seats = current_seats;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeparture_county() {
        return departure_county;
    }

    public void setDeparture_county(String departure_county) {
        this.departure_county = departure_county;
    }

    public String getDriving_name() {
        return driving_name;
    }

    public void setDriving_name(String driving_name) {
        this.driving_name = driving_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getDeparture_city() {
        return departure_city;
    }

    public void setDeparture_city(String departure_city) {
        this.departure_city = departure_city;
    }

    public String getDestination_city() {
        return destination_city;
    }

    public void setDestination_city(String destination_city) {
        this.destination_city = destination_city;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getBoarding_point() {
        return boarding_point;
    }

    public void setBoarding_point(String boarding_point) {
        this.boarding_point = boarding_point;
    }

    public String getBreakout_point() {
        return breakout_point;
    }

    public void setBreakout_point(String breakout_point) {
        this.breakout_point = breakout_point;
    }

    public int getBooking_seats() {
        return booking_seats;
    }

    public void setBooking_seats(int booking_seats) {
        this.booking_seats = booking_seats;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getDeparture_address_code() {
        return departure_address_code;
    }

    public void setDeparture_address_code(int departure_address_code) {
        this.departure_address_code = departure_address_code;
    }

    public int getDestination_address_code() {
        return destination_address_code;
    }

    public void setDestination_address_code(int destination_address_code) {
        this.destination_address_code = destination_address_code;
    }
}
