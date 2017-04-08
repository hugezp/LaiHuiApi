package com.cyparty.laihui.domain;

/**
 * Created by dupei on 2017/4/8 0008.
 */
//车主行驶证认证
public class UserTravelCardInfo {
    private int _id;
    private int user_id;//车主用户id
    private String car_license_number;//车牌号
    private String car_color;//颜色
    private String registration_date;//注册时间
    private String create_time;//信息提交时间
    private String car_type;//品牌类型
    private String vehicle_owner_name;//车辆所有人
    private String travel_license_photo;//驾驶证照片oss路径
    private String is_enable;//是否已经被处理 0：已处理 1：未处理

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

    public String getCar_license_number() {
        return car_license_number;
    }

    public void setCar_license_number(String car_license_number) {
        this.car_license_number = car_license_number;
    }

    public String getCar_color() {
        return car_color;
    }

    public void setCar_color(String car_color) {
        this.car_color = car_color;
    }

    public String getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(String registration_date) {
        this.registration_date = registration_date;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getVehicle_owner_name() {
        return vehicle_owner_name;
    }

    public void setVehicle_owner_name(String vehicle_owner_name) {
        this.vehicle_owner_name = vehicle_owner_name;
    }

    public String getTravel_license_photo() {
        return travel_license_photo;
    }

    public void setTravel_license_photo(String travel_license_photo) {
        this.travel_license_photo = travel_license_photo;
    }

    public String getIs_enable() {
        return is_enable;
    }

    public void setIs_enable(String is_enable) {
        this.is_enable = is_enable;
    }
}
