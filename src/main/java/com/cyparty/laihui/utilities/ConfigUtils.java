package com.cyparty.laihui.utilities;

/**
 * Created by zhu on 2016/10/27.
 */
public class ConfigUtils {
    public static final int DRIVER_DEPARTURE_COUNTS = 50;//每日预定车单次数
    public static final int DRIVER_GRAD_ORDER_COUNTS = 50;//每日预定车单次数
    public static final int PASSENGER_DEPARTURE_COUNTS = 50;//每日发布车单次数
    public static final int BOOKING_COUNTS = 50;//每日预定车单次数
    public static final double QUERY_DISTANCE = 20000.0;//附近搜索范围
    public static final double SERVICE_FEE = 5.0;//乘客必达车单服务费
    public static final double ORDER_TIMEOUT = 15;//订单支付超时(15分钟)
    public static final String PROFESSIONAL_PROMOTION = "https://h5.laihuipinche.com/laihui/share/spread2?code=";//专业推广url
    public static final String NATIONAL_AGENT = "https://h5.laihuipinche.com/share_spread?token=";//全民代理url
    //极光推送离线时长 单位:秒
    public static final long TIME_TO_LIVE = 1800;
    //极光环境 false测试 true正式
    public static final boolean JPUSH_PROD = false;
    //极光AppKey
    public static final String JPUSH_APP_KEY = "bdc7c59bbaba335fe3593f1f";
    //极光秘钥
    public static final String JPUSH_MASTER_SECRET = "bd73a921b52dad9447262d5f";
}
