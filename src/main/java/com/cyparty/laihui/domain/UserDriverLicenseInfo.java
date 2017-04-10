package com.cyparty.laihui.domain;

/**
 * Created by dupei on 2017/4/8 0008.
 */
//车主驾驶证认证信息
public class UserDriverLicenseInfo {
    private int _id;
    private int user_id;//车主用户id
    private String driver_name;//司机姓名
    private String driver_license_number;//驾驶证号码
    private String first_issue_date;//初次领证日期
    private String allow_car_type;//准驾车型
    private String effective_date_start;//有效期始
    private String effective_date_end;//有效期始
    private String create_time;//创建时间
    private String driver_license_photo;//驾驶证照片oss路径
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

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_license_number() {
        return driver_license_number;
    }

    public void setDriver_license_number(String driver_license_number) {
        this.driver_license_number = driver_license_number;
    }

    public String getFirst_issue_date() {
        return first_issue_date;
    }

    public void setFirst_issue_date(String first_issue_date) {
        this.first_issue_date = first_issue_date;
    }

    public String getAllow_car_type() {
        return allow_car_type;
    }

    public void setAllow_car_type(String allow_car_type) {
        this.allow_car_type = allow_car_type;
    }

    public String getEffective_date_start() {
        return effective_date_start;
    }

    public void setEffective_date_start(String effective_date_start) {
        this.effective_date_start = effective_date_start;
    }

    public String getEffective_date_end() {
        return effective_date_end;
    }

    public void setEffective_date_end(String effective_date_end) {
        this.effective_date_end = effective_date_end;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getDriver_license_photo() {
        return driver_license_photo;
    }

    public void setDriver_license_photo(String driver_license_photo) {
        this.driver_license_photo = driver_license_photo;
    }

    public String getIs_enable() {
        return is_enable;
    }

    public void setIs_enable(String is_enable) {
        this.is_enable = is_enable;
    }
}
