package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/8/15.
 */
public class ErrorCode {
    /**
     * 登陆模块错误代码
     */
    public static final int TOKEN_EXPIRED = 301; //无效token
    public static final int SMS_SEND_FAILED = 305; //验证码发送失败
    public static final int SMS_TIMES_LIMIT = 306; //发送验证码过于频繁
    public static final int SMS_CHECKED_FAILED = 307;//验证码校验失败

    public static final int DEPARTURE_ORDER_EXPIRED = 309; //司机发车单失效
    public static final int DEPARTURE_ORDER_UNENABLED = 310; //司机发车单座位已满

    public static final int PARAMETER_WRONG = 401; //获取参数有误
    public static final int IS_CAR_OWNER = 403; //车主认证状态有误
    public static final int IS_VALIDATED = 402; //身份认证状态有误
    public static final int BOOKING_TIMES_LIMIT = 405; //预定车单次数超过限制
    public static final int BOOKING_ORDER_IS_EXISTING = 407; //当前存在订单，提醒先删除当前订单
    public static final int BOOKING_ORDER_IS_NOT_EXISTING = 406; //当前订单不存在
    public static final int ORDER_TIMEOUT = 408; //订单支付超时
    public static final int ORDER_IS_ALEARDY_GRABED = 501; //当前订单不存在
    public static final int ORDER_GRABED_UNABLE_CANCLE = 502; //当前订单不存在
    public static final int ORDER_IS_SELF = 100; //司机不能抢自己的单子
    public static final int INVITE_TIME = 601; //邀请过于频繁
    public static final int NO_DATA = 408;//暂无数据
    public static final int ERROR_SYSTEM = 500;//系统错误

















}
