package com.cyparty.laihui.utilities;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/10 0010.
 */
public class ValidateUtils {
    @Autowired
    OssUtil ossUtil;
    public static  String getUrl(String car_id,String name){
        String result= null;
        String host = "http://idcard3.market.alicloudapi.com";
        String path = "/idcardAudit";
        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE 3bd0bd94a2f24acaa375dc1e7f44ea9f");
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("idcard", car_id);
        querys.put("name", name);
        HttpResponse response = null;
        try {
            response = HttpUtils.doGet(host, path, method, headers, querys);
            result = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
