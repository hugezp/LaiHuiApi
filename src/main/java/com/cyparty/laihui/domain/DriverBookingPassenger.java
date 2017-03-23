package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/9/8.
 * 车主订单与乘客订单关系
 */
public class DriverBookingPassenger {
    private int _id;
    private int driver_id;
    //车主订单
    private int driver_order_id;
    //乘客订单
    private int passenger_order_id;
    //订单状态
    private int status;
    //预定座位数
    private int booking_seats;
    //创建时间
    private String  create_time;

    public int getBooking_seats() {
        return booking_seats;
    }

    public void setBooking_seats(int booking_seats) {
        this.booking_seats = booking_seats;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
