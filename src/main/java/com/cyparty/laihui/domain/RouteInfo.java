package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/8/3.
 * 路线信息
 */
public class RouteInfo {
    private int r_id;
    private int is_enable;//是否可行
    private String  departure;//出发地
    private String  destination;//描述
    private float  driving_length;//行驶公里数
    private double  cost;//花费
    private String create_time;//创建时间

    public int getIs_enable() {
        return is_enable;
    }

    public void setIs_enable(int is_enable) {
        this.is_enable = is_enable;
    }

    public int getR_id() {
        return r_id;
    }

    public void setR_id(int r_id) {
        this.r_id = r_id;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public float getDriving_length() {
        return driving_length;
    }

    public void setDriving_length(float driving_length) {
        this.driving_length = driving_length;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
