package com.cyparty.laihui.utilities;

/**
 * Created by zhu on 2016/10/27.
 */
public class ConfigUtils {
    private static final int driver_departure_counts=5;//每日预定车单次数
    private static final int driver_grad_order_counts=5;//每日预定车单次数
    private static final int passenger_departure_counts=5;//每日发布车单次数
    private static final int booking_counts=5;//每日预定车单次数
    private static final double query_distance=20000.0;//附近搜索范围
    private static final double SERVICE_FEE=5.0;//乘客必达车单服务费
    private static final double ORDER_TIMEOUT=15;//订单支付超时(15分钟)
    public static final String PROFESSIONAL_PROMOTION = "https://h5.laihuipinche.com/laihui/share/spread2?code=";//专业推广url
    public static final String NATIONAL_AGENT = "https://h5.laihuipinche.com/share_spread?token=";//全民代理url

    public static double getQuery_distance() {
        return query_distance;
    }
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

    public static double getServiceFee() {
        return SERVICE_FEE;
    }

    public static double getOrderTimeout() {
        return ORDER_TIMEOUT;
    }

    public static String getProfessionalPromotion() {
        return PROFESSIONAL_PROMOTION;
    }

    public static String getNationalAgent() {
        return NATIONAL_AGENT;
    }
}
