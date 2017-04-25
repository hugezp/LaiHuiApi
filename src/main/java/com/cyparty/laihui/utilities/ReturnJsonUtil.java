package com.cyparty.laihui.utilities;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.Carousel;

/**
 * Created by Shadow on 2016/5/3.
 */
public class ReturnJsonUtil {
    /**
     * 返回成功信息
     *
     * @param result        需要返回的值
     * @param error_message 提示信息
     * @return
     */
    public static String returnFailJsonString(JSONObject result, String error_message) {
        JSONObject item = new JSONObject();
        item.put("message", error_message);
        item.put("status", false);
        item.put("result", result);
        String jsonString = JSON.toJSONString(item);
        return jsonString;
    }

    /**
     * 返回失败信息
     *
     * @param result  需要返回的值
     * @param message 提示信息
     * @return
     */
    public static String returnSuccessJsonString(JSONObject result, String message) {
        JSONObject item = new JSONObject();
        item.put("message", message);
        item.put("status", true);
        item.put("result", result);
        String jsonString = item.toJSONString();
        return jsonString;
    }

    //闪屏和弹出广告json
    public static JSONObject getCarouselJson(AppDB appDB) {
        JSONObject result_json = new JSONObject();
        String where = " where pc_type = 2 order by pc_image_seq desc,pc_image_create_time desc limit 1";
        String flashWhere = "where pc_type = 4 order by pc_image_create_time desc limit 1";
        //弹出广告
        Carousel carousel = appDB.getCarouselObj(where);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", carousel.get_id());
        jsonObject.put("image_url", carousel.getImage_url());
        jsonObject.put("image_link", carousel.getImage_link());
        jsonObject.put("image_title", carousel.getImage_title());
        result_json.put("slides", carousel);
        //闪屏
        Carousel splashScreen = appDB.getCarouselObj(flashWhere);
        JSONObject flash = new JSONObject();
        flash.put("flash_id",splashScreen.get_id());
        flash.put("flash_url", splashScreen.getImage_url());
        flash.put("flash_link", splashScreen.getImage_link());
        flash.put("flash_title", splashScreen.getImage_title());
        result_json.put("flash", flash);
        return result_json;
    }
}
