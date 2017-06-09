package com.cyparty.laihui.utilities.weather;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class Test{
    public static void main(String[] args) throws UnsupportedEncodingException {
        String strCity = URLEncoder.encode("郑州", "GB2312");
        String link = "http://php.weather.sina.com.cn/xml.php?city="+strCity+"&password=DJOYnieT8234jlsK&day=0";
        URL url;
        try {
            url = new URL(link);
            System.out.println(url);
            XmlParser parser = new XmlParser(url);
            String[] nodes = {"status1","temperature1","temperature2"};
            Map<String, String> map = parser.getValue(nodes);
            //System.out.println(map.get(nodes[0]));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}

