package com.cyparty.laihui.utilities;

import net.sf.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Created by Administrator on 2017/3/4.
 */
public class MapTransUtil {
    /**
     * 逆向匹配接口
     * 根据地址名称，匹配得到经纬度坐标
     * @param addr
     * @return
     */
    public static String geocode(String addr){
        String xyStr="";
        addr= URLEncoder.encode(addr);
        StringBuffer str = new StringBuffer();
        String lng="";
        String lat="";
        try {
            //System.out.println("http://api.map.baidu.com/geocoder?address="+addr+"&output=json");
            String sUrl="http://api.map.baidu.com/geocoder/v2/?address="+addr+"&output=json&ak=AEoLCgHKzp9SFUjdSXgoED6vpTVSDq4T";
            URL url = new URL(sUrl);
            //java.net.URL url = new java.net.URL("http://api.map.baidu.com/?qt=gc&wd="+addr+"&cn=%E5%85%A8%E5%9B%BD&ie=utf-8&oue=0&res=api&callback=BMap._rd._cbk96117");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            in.close();
            JSONObject dataJson =JSONObject.fromObject(str.toString());
            JSONObject result = dataJson.getJSONObject("result");
            JSONObject location = result.getJSONObject("location");
            lng =location.getDouble("lng")+"";
            lat =location.getDouble("lat")+"";
            xyStr= lng+" "+lat;
        } catch (Exception e) {
            lng="";
            lat="";
        }
        return xyStr;
    }

    public static void main(String [] args){
        String xy = geocode("南四环G107(公交站)");
        System.out.println(xy);



    }
}
