package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/12/6.
 * 对应数据库中的 pc_order   pay_cash_log pc_user pc_passenger_publish_info表
 */
public class Order {
    private int _id;
    private int user_id;
    private int driver_id;
    private int order_id;
    //行程是否结束
    private int is_complete;
    //订单状态
    private int order_status;
    //订单类型
    private int order_type;
    //是否可用
    private int is_enable;
    //iOS 或者Android
    private int source;
    //创建时间
    private String create_time;
    //用户名
    private String user_name;
    //用户手机号
    private String user_mobile;
    //用户身份证
    private String user_idsn;
    //用户头像
    private String user_avatar;
    //起点
    private String boarding_point;
    //终点
    private String breakout_point;
    //描述
    private String description;
    //金额
    private double price;
    //预定座位数
    private int booking_seats;
    //出发时间
    private String departure_time;
    //更新时间
    private String update_time;
    //备注
    private String remark;
    //订单号
    private String tradeNo;
    //是否是必答车单 0 不是 1 是
    private int isArrive;
    private int surchargeType;
    private double surchargeMoney;

    public int getSurchargeType() {
        return surchargeType;
    }

    public void setSurchargeType(int surchargeType) {
        this.surchargeType = surchargeType;
    }

    public double getSurchargeMoney() {
        return surchargeMoney;
    }

    public void setSurchargeMoney(double surchargeMoney) {
        this.surchargeMoney = surchargeMoney;
    }

    public int getIsArrive() {
        return isArrive;
    }

    public void setIsArrive(int isArrive) {
        this.isArrive = isArrive;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public int getIs_complete() {
        return is_complete;
    }

    public void setIs_complete(int is_complete) {
        this.is_complete = is_complete;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getBooking_seats() {
        return booking_seats;
    }

    public void setBooking_seats(int booking_seats) {
        this.booking_seats = booking_seats;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_idsn() {
        return user_idsn;
    }

    public void setUser_idsn(String user_idsn) {
        this.user_idsn = user_idsn;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
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

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getOrder_status() {
        return order_status;
    }

    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public int getIs_enable() {
        return is_enable;
    }

    public void setIs_enable(int is_enable) {
        this.is_enable = is_enable;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }
}
