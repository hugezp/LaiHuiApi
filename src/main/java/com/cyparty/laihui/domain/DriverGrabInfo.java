package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/7/25.
 * 车主抢单信息
 * 需要几个表关联
 * pc_user_token   pc_passenger_publish_info  pc_orders  pc_driver_publish_info  pc_user
 *
 */
public class DriverGrabInfo {
    //source Android 或者ios _id 订单id,车主id,状态,乘客订单id
    private int user_id,_id,driver_id,status,source,passenger_order_id;
    // 车主信息 姓名，身份证，驾驶证，行驶证，车牌，车主，品牌，车型，车颜色，车注册时间，车有效时间，
    private String car_owner_name,idsn,pic_licence,pic_licence2,car_id,car_owner,car_brand,car_type,car_color,car_reg_date,car_validate_date,reason;
    private String token;//用户token值
    private String checked_time,create_time;//车检时间 ，创建时间
    private String user_name;//用户名
    private String user_avatar,user_mobile;//乘客的头像，乘客手机号

    public int getPassenger_order_id() {
        return passenger_order_id;
    }

    public void setPassenger_order_id(int passenger_order_id) {
        this.passenger_order_id = passenger_order_id;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_name() {
        return user_name;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public String getChecked_time() {
        return checked_time;
    }

    public void setChecked_time(String checked_time) {
        this.checked_time = checked_time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getCar_owner_name() {
        return car_owner_name;
    }

    public void setCar_owner_name(String car_owner_name) {
        this.car_owner_name = car_owner_name;
    }

    public String getIdsn() {
        return idsn;
    }

    public void setIdsn(String idsn) {
        this.idsn = idsn;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPic_licence() {
        return pic_licence;
    }

    public void setPic_licence(String pic_licence) {
        this.pic_licence = pic_licence;
    }

    public String getPic_licence2() {
        return pic_licence2;
    }

    public void setPic_licence2(String pic_licence2) {
        this.pic_licence2 = pic_licence2;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public String getCar_owner() {
        return car_owner;
    }

    public void setCar_owner(String car_owner) {
        this.car_owner = car_owner;
    }

    public String getCar_brand() {
        return car_brand;
    }

    public void setCar_brand(String car_brand) {
        this.car_brand = car_brand;
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

    public String getCar_reg_date() {
        return car_reg_date;
    }

    public void setCar_reg_date(String car_reg_date) {
        this.car_reg_date = car_reg_date;
    }

    public String getCar_validate_date() {
        return car_validate_date;
    }

    public void setCar_validate_date(String car_validate_date) {
        this.car_validate_date = car_validate_date;
    }
}
