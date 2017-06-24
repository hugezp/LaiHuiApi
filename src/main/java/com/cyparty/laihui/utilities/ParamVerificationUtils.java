package com.cyparty.laihui.utilities;


import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * 参数验证模块
 * Created by pangzhenpeng on 2017/6/23.
 */
public class ParamVerificationUtils {

    /**
     * 验证字符串是否为空
     *
     * @param string
     * @return true不为空, false为空
     */
    public static boolean isOrNotEmpty(String string) {
        boolean flag = false;
        if (string != null && !string.equals(""))
            flag = true;
        return flag;
    }


    /**
     * 根据是否支付成功确定行程发布成功与否接口参数验证
     *
     * @return -1表示参数不完整 0表示手机号格式错误 1通过验证
     */
    public static int judgmentValidation(HttpServletRequest request) {
        int count = 0;
        String tradeNo = request.getParameter("tradeNo");
        String releaseTime = request.getParameter("releaseTime");
        String flag = request.getParameter("flag");
        if (isOrNotEmpty(tradeNo) && isOrNotEmpty(releaseTime) && isOrNotEmpty(flag)) {
            count = 1;
        }
        return count;
    }

    /**
     * 乘客发布行程接口参数验证
     */
    public static int insertItineraryValidation(HttpServletRequest request) {
        int count = 0;
        String boarding_point = request.getParameter("boarding_point");
        String breakout_point = request.getParameter("breakout_point");
        String booking_seats = request.getParameter("booking_seats");
        String start_time = request.getParameter("departure_time");//出发时间
        String price = request.getParameter("price");
        if (isOrNotEmpty(boarding_point) && isOrNotEmpty(breakout_point)
                && isOrNotEmpty(booking_seats) && isOrNotEmpty(start_time) && isOrNotEmpty(price)) {
            count = 1;
        }
        return count;
    }
}
