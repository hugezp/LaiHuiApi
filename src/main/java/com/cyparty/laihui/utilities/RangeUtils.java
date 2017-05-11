package com.cyparty.laihui.utilities;


import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/3/4.
 */
public class RangeUtils {

    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s*1000;
        return s;
    }
    public static String getSuitability(double my_distance,double start_point_distance,double end_point_distance){
        DecimalFormat df   = new DecimalFormat("######0.00");
        double match = (start_point_distance+end_point_distance)/my_distance;
        double matchs = match<1?match:1;
        String suitability = df.format(100-matchs*100) + "%";
        return suitability;
    }
    public static void main (String[]args){
        System.out.print(getSuitability(101,2,3));
    }
}

