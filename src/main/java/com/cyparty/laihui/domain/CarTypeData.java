package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/7/27.
 * 对应数据库 pc_car_type 表
 */
public class CarTypeData {
    private int _id;
    private String car_font_letter;
    private String logo;
    private String name;

    private String brand_id;
    private String brand_type_id;
    private String brand_type_name;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getBrand_type_id() {
        return brand_type_id;
    }

    public void setBrand_type_id(String brand_type_id) {
        this.brand_type_id = brand_type_id;
    }

    public String getBrand_type_name() {
        return brand_type_name;
    }

    public void setBrand_type_name(String brand_type_name) {
        this.brand_type_name = brand_type_name;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getCar_font_letter() {
        return car_font_letter;
    }

    public void setCar_font_letter(String car_font_letter) {
        this.car_font_letter = car_font_letter;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
