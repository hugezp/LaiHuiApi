package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/12/2.
 */
public class PCCount {
    private int driver_departure_count;//司机发布出行
    private int passenger_departure_count;//乘客发布出行
    private int driver_grab_count;//司机抢单
    private int passenger_booking_count;//乘客预定
    private int total;//总计拼车次数

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getDriver_departure_count() {
        return driver_departure_count;
    }

    public void setDriver_departure_count(int driver_departure_count) {
        this.driver_departure_count = driver_departure_count;
    }

    public int getPassenger_departure_count() {
        return passenger_departure_count;
    }

    public void setPassenger_departure_count(int passenger_departure_count) {
        this.passenger_departure_count = passenger_departure_count;
    }

    public int getDriver_grab_count() {
        return driver_grab_count;
    }

    public void setDriver_grab_count(int driver_grab_count) {
        this.driver_grab_count = driver_grab_count;
    }

    public int getPassenger_booking_count() {
        return passenger_booking_count;
    }

    public void setPassenger_booking_count(int passenger_booking_count) {
        this.passenger_booking_count = passenger_booking_count;
    }
}
