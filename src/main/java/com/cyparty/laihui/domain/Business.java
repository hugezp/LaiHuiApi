package com.cyparty.laihui.domain;

/**
 * Created by Administrator on 2017/4/13.
 */
public class Business {
    private int _id;//记录ID
    private String business_name;
    private String business_mobile;
    private String address;
    private String cooperation_way;
    private String cooperation_description;
    private String create_time;
    private int flag;

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBusiness_mobile() {
        return business_mobile;
    }

    public void setBusiness_mobile(String business_mobile) {
        this.business_mobile = business_mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCooperation_way() {
        return cooperation_way;
    }

    public void setCooperation_way(String cooperation_way) {
        this.cooperation_way = cooperation_way;
    }

    public String getCooperation_description() {
        return cooperation_description;
    }

    public void setCooperation_description(String cooperation_description) {
        this.cooperation_description = cooperation_description;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
