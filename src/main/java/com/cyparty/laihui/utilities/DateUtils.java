package com.cyparty.laihui.utilities;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/8.
 */
public class DateUtils {
    public static String getTimesToNow(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = format.format(new Date());
        String returnText = null;
        try {
            long from = format.parse(date).getTime();
            long to = format.parse(now).getTime();
            int days = (int) ((to - from)/(1000 * 60 * 60 * 24));
            if(days == 0){//一天以内，以分钟或者小时显示
                int hours = (int) ((to - from)/(1000 * 60 * 60));
                if(hours == 0){
                    int minutes = (int) ((to - from)/(1000 * 60));
                    if(minutes == 0){
                        returnText = "刚刚";
                    }else{
                        returnText = minutes + "分钟前";
                    }
                }else{
                    returnText = hours + "小时前";
                }
            } else if(days == 1){
                returnText = "昨天";
            }else{
                returnText = days + "天前";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return returnText;
    }
    public static int getTimesToNow1(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = format.format(new Date());
        int minutes = 0;
        try {
            long from = format.parse(date).getTime();
            long to = format.parse(now).getTime();
            minutes = (int) ((to - from)/(1000 * 60));
         } catch (ParseException e) {
            e.printStackTrace();
        }

        return minutes;
    }

    public static String  getProcessdTime(String time){
        //String time = "2017-01-02 14:56:22";
        String []s = time.replace("-"," ").replace(":"," ").split(" ");
        String processdTime = s[1]+"月"+s[2]+"日 "+s[3]+":"+" "+s[4];
       return processdTime;
    }
    public static void main(String[]args){
       // System.out.print(getTimesToNow("2017-03-07 15:02:22"));
        getProcessdTime("");

    }
}


