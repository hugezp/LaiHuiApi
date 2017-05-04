package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/8/15.
 */
public class

ErrorCode {
    /**
     * 登陆模块错误代码
     */
    private static final int token_expired = 301; //无效token
    private static final int sms_send_failed = 305; //验证码发送失败
    private static final int sms_times_limit = 306; //发送验证码过于频繁
    private static final int sms_checked_failed = 307;//验证码校验失败

    private static final int departure_order_expired = 309; //司机发车单失效
    private static final int departure_order_unEnabled = 310; //司机发车单座位已满

    private static final int parameter_wrong = 401; //获取参数有误
    private static final int is_car_owner = 403; //车主认证状态有误
    private static final int is_validated = 402; //身份认证状态有误
    private static final int booking_times_limit = 405; //预定车单次数超过限制
    private static final int booking_order_is_existing = 407; //当前存在订单，提醒先删除当前订单
    private static final int booking_order_is_not_existing = 406; //当前订单不存在
    private static final int order_is_aleardy_grabed = 501; //当前订单不存在
    private static final int order_grabed_unable_cancle = 502; //当前订单不存在
    private static final int order_is_self = 100; //司机不能抢自己的单子
    private static final int invite_time = 601; //邀请过于频繁

    private static final int no_data = 408;

    public static int getInvite_time() {
        return invite_time;
    }


    public static int getOrder_grabed_unable_cancle() {
        return order_grabed_unable_cancle;
    }

    public static int getOrder_is_self() {
        return order_is_self;
    }

    public static int getOrder_is_aleardy_grabed() {
        return order_is_aleardy_grabed;
    }

    public static int getToken_expired() {
        return token_expired;
    }

    public static int getSms_send_failed() {
        return sms_send_failed;
    }

    public static int getSms_times_limit() {
        return sms_times_limit;
    }

    public static int getSms_checked_failed() {
        return sms_checked_failed;
    }

    public static int getParameter_wrong() {
        return parameter_wrong;
    }

    public static int getIs_car_owner() {
        return is_car_owner;
    }

    public static int getBooking_times_limit() {
        return booking_times_limit;
    }

    public static int getBooking_order_is_existing() {
        return booking_order_is_existing;
    }

    public static int getDeparture_order_expired() {
        return departure_order_expired;
    }

    public static int getDeparture_order_unEnabled() {
        return departure_order_unEnabled;
    }

    public static int getBooking_order_is_not_existing() {
        return booking_order_is_not_existing;
    }

    public static int getIs_validated() {
        return is_validated;
    }

    public static int getNo_data() {
        return no_data;
    }
}
