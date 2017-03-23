package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/9/12.
 */
public class UserHistory {
    private int _id;
    private String mobile;//手机号
    private int driver_order_id;//车主车单id
    private int passenger_order_id;//乘客车单id
    private int booking_order_id;//订单id
    private int order_source;//订单来源
    private int order_type;//订单类型

    public int getBooking_order_id() {
        return booking_order_id;
    }

    public void setBooking_order_id(int booking_order_id) {
        this.booking_order_id = booking_order_id;
    }

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getDriver_order_id() {
        return driver_order_id;
    }

    public void setDriver_order_id(int driver_order_id) {
        this.driver_order_id = driver_order_id;
    }

    public int getPassenger_order_id() {
        return passenger_order_id;
    }

    public void setPassenger_order_id(int passenger_order_id) {
        this.passenger_order_id = passenger_order_id;
    }

    public int getOrder_source() {
        return order_source;
    }

    public void setOrder_source(int order_source) {
        this.order_source = order_source;
    }
}
