package com.cyparty.laihui.utilities;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.Carousel;

import java.util.List;

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

    //得到轮播图json
    public static JSONObject getCarouselJson(AppDB appDB, int page, int size, int id, int type) {
        JSONObject result_json = new JSONObject();
        JSONArray dataArray = new JSONArray();
        JSONArray flashArray = new JSONArray();
        //pc_image_create_time DESC,

        String where = " where pc_type=" + type + " order by pc_image_seq ASC ";
        String flashWhere = "where pc_type = 4 order by pc_image_create_time desc limit 1";
        int offset = page * size;
        int count = 1;
        if (id == 0) {
            count = appDB.getCarousel(where).size();
            where = where + " limit " + offset + "," + size;
        } else {
            where = " where _id=" + id;
        }
        List<Carousel> carouselList = appDB.getCarousel(where);
        for (Carousel carousel : carouselList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", carousel.get_id());
            jsonObject.put("image_seq", carousel.getSeq());
            jsonObject.put("image_url", carousel.getImage_url());
            jsonObject.put("image_link", carousel.getImage_link());
            jsonObject.put("image_title", carousel.getImage_title());
            jsonObject.put("create_time", carousel.getCreate_time());
            dataArray.add(jsonObject);
        }
        Carousel splashScreen = appDB.getCarouselObj(flashWhere);
        JSONObject flash = new JSONObject();
        flash.put("flash_url", splashScreen.getImage_url());
        flash.put("flash_link", splashScreen.getImage_link());
        flash.put("flash_title", splashScreen.getImage_title());
        flash.put("flash_subtitle", splashScreen.getImage_subtitle());
        result_json.put("total", count);
        result_json.put("page", page);
        result_json.put("size", size);
        result_json.put("slides", dataArray);
        result_json.put("flash", flash);
        return result_json;
    }
}
