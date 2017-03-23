package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/9/10.
 */
public class UserRoleAction {
    private int _id;
    private int driver_order_id;//车主车单id
    private int passenger_order_id;//乘客车单id
    private int booking_order_id;//订单id
    private int order_type;//订单类型
    private int order_source;//订单来源
    private String user_mobile;//手机号
    private String create_time;

    public int getBooking_order_id() {
        return booking_order_id;
    }

    public void setBooking_order_id(int booking_order_id) {
        this.booking_order_id = booking_order_id;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public int getOrder_source() {
        return order_source;
    }

    public void setOrder_source(int order_source) {
        this.order_source = order_source;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
