package com.cyparty.laihui.TestUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/7.
 */
public class SmsConfig {
    static List<String> list = new ArrayList<>();
    static List<String> list1 = new ArrayList<>();
    public static List<String> config(){
       list.add("13298172885");
        list.add("15639356022");
        return list;
    }
    public static List<String> ipConfig(){
        list1.add("192.168.1.120");
        list1.add("192.168.1.112");
        list1.add("192.168.1.103");
        list1.add("192.168.1.106");
        list1.add("192.168.1.114");
        list1.add("192.168.191.2");
        list1.add("192.168.1.162");


        return list1;
    }
}
