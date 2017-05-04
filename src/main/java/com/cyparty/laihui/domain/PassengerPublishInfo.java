package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/8/23.
 */
public class PassengerPublishInfo {
    private int _id;
    private int user_id;//乘客用户id
    private String departure_city;//出发城市
    private String destination_city;//目的城市
    private String departure;//出发地
    private String destination;//目的地
    private String start_time;//开始时间
    private String end_time;//结束时间
    private String boarding_point;//起点
    private String breakout_point;//终点
    private int booking_seats;//预定座位
    private String description;//描述
    private String create_time;//创建时间
    private double price;//价格
    private String mobile;//手机号
//    private String name;
//    private String idsn;
//    private String name;
//    private String idsn;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
