package com.cyparty.laihui.domain;

/**
 * Created by Administrator on 2017/3/9.
 * 对应数据库中  pc_driver_publish_info
 */
public class CommonRoute {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    //车主ID
    private int user_id;
    //是否可用
    private int is_enable;
    //destinat_city 终点城市  departure_city 起点城市
    // departure_lon 起点经度  departure_lat 起点纬度  destinat_lon终点经度 destinat_lat终点纬度
    //departure_address 起点地址 destinat_address 终点地址
    private String departure_city, departure_address, departure_lon, departure_lat, destinat_city, destinat_address, destinat_lon, destinat_lat;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getIs_enable() {
        return is_enable;
    }

    public void setIs_enable(int is_enable) {
        this.is_enable = is_enable;
    }

    public String getDeparture_city() {
        return departure_city;
    }

    public void setDeparture_city(String departure_city) {
        this.departure_city = departure_city;
    }

    public String getDeparture_address() {
        return departure_address;
    }

    public void setDeparture_address(String departure_address) {
        this.departure_address = departure_address;
    }

    public String getDeparture_lon() {
        return departure_lon;
    }

    public void setDeparture_lon(String departure_lon) {
        this.departure_lon = departure_lon;
    }

    public String getDeparture_lat() {
        return departure_lat;
    }

    public void setDeparture_lat(String departure_lat) {
        this.departure_lat = departure_lat;
    }

    public String getDestinat_city() {
        return destinat_city;
    }

    public void setDestinat_city(String destinat_city) {
        this.destinat_city = destinat_city;
    }

    public String getDestinat_address() {
        return destinat_address;
    }

    public void setDestinat_address(String destinat_address) {
        this.destinat_address = destinat_address;
    }

    public String getDestinat_lon() {
        return destinat_lon;
    }

    public void setDestinat_lon(String destinat_lon) {
        this.destinat_lon = destinat_lon;
    }

    public String getDestinat_lat() {
        return destinat_lat;
    }

    public void setDestinat_lat(String destinat_lat) {
        this.destinat_lat = destinat_lat;
    }
}
