package com.cyparty.laihui.utilities;

/**
 * Created by zhu on 2016/10/27.
 */
public class ConfigUtils {
    private static final int driver_departure_counts=10;//每日预定车单次数
    private static final int driver_grad_order_counts=10;//每日预定车单次数
    private static final int passenger_departure_counts=10;//每日预定车单次数
    private static final int booking_counts=10;//每日预定车单次数

    public static int getBooking_counts() {
        return booking_counts;
    }

    public static int getDriver_departure_counts() {
        return driver_departure_counts;
    }

    public static int getDriver_grad_order_counts() {
        return driver_grad_order_counts;
    }

    public static int getPassenger_departure_counts() {
        return passenger_departure_counts;
    }
}
