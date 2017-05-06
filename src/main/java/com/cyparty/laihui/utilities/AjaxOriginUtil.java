package com.cyparty.laihui.utilities;

import org.springframework.http.HttpHeaders;

/**
 * 解决ajax的跨域问题
 * Created by YangGuang on 2017/5/6 .
 */
public class AjaxOriginUtil {

    /**
     * 构造方法私有化
     */
    private AjaxOriginUtil() {

    }

    public static HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        return responseHeaders;
    }
}
