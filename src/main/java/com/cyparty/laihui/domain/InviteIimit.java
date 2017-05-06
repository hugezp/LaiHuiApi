package com.cyparty.laihui.domain;

/**
 * Created by Administrator on 2017/3/17.
 */
public class InviteIimit {
    private int _id;
    private int passenger_id;
    private int driver_id;
    private int driver_car_id;
    private int passenger_car_id;
    private String invite_time;
    private double price;

    public int getDriver_car_id() {
        return driver_car_id;
    }

    public void setDriver_car_id(int driver_car_id) {
        this.driver_car_id = driver_car_id;
    }

    public int getPassenger_car_id() {
        return passenger_car_id;
    }

    public void setPassenger_car_id(int passenger_car_id) {
        this.passenger_car_id = passenger_car_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getPassenger_id() {
        return passenger_id;
    }

    public void setPassenger_id(int passenger_id) {
        this.passenger_id = passenger_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getInvite_time() {
        return invite_time;
    }

    public void setInvite_time(String invite_time) {
        this.invite_time = invite_time;
    }
}
