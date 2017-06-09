package com.cyparty.laihui.utilities.weather;

import com.alibaba.fastjson.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by pangzhenpeng on 2017/6/8.
 */
public class GetWeather {
    private static GetWeather getWeather = null;
    private GetWeather (){}
    public static GetWeather getInstance() {
        if (getWeather == null) {
            getWeather = new GetWeather();
        }
        return getWeather;
    }
    //static String[] city = {"北京", "天津", "上海", "重庆", "石家庄", "太原", "沈阳", "长春", "哈尔滨", "南京", "杭州", "合肥", "福州", "南昌", "济南", "郑州", "武汉", "长沙", "广州", "海口", "成都", "贵阳", "昆明", "西安", "兰州", "西宁", "拉萨", "南宁", "呼和浩特", "银川", "乌鲁木齐", "香港", "台北", "澳门"};  //各个城市
    static int[] day = {0};   //哪一天的天气
    static String weather;  //保存天气情况
    static String high;  //保存当天最高温度
    static String low;  //保存当天最低温度
    JSONObject weatherJson = new JSONObject();

    public JSONObject getweather(String cityName)   //获取天气函数
    {
        URL ur;

        try {

            DocumentBuilderFactory domfac;  //相关这个类的使用，可以去网上搜索，下同，不做详细介绍
            domfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            Document doc;
            Element root;
            NodeList books;
            for (int i=0;i<day.length;i++){
                String strCity = URLEncoder.encode(cityName, "GB2312");
                ur = new URL("http://php.weather.sina.com.cn/xml.php?city=" + strCity + "&password=DJOYnieT8234jlsK&day=" + i);
                doc = dombuilder.parse(ur.openStream());
                root = doc.getDocumentElement();
                books = root.getChildNodes();
                for (Node node = books.item(1).getFirstChild(); node != null; node = node.getNextSibling()) {
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        if (node.getNodeName().equals("status1")) weather = node.getTextContent();  //获取到天气情况
                        else if (node.getNodeName().equals("temperature1")) high = node.getTextContent();  //获取到最高温度
                        else if (node.getNodeName().equals("temperature2")) low = node.getTextContent();   //获取到最低温度
                    }
                }
                weatherJson.put("message","获取天气信息成功！");
                weatherJson.put("cityName",cityName);
                weatherJson.put("weather",weather);
                weatherJson.put("low",low);
                weatherJson.put("high",high);
                System.out.println(cityName + " " + weather + " " + low + "℃~" + high + "℃");  //前台输出

            }
        } catch (Exception e) {
            weatherJson.put("message","获取天气信息失败！");
            weatherJson.put("cityName","");
            weatherJson.put("weather","");
            weatherJson.put("low","");
            weatherJson.put("high","");
        }
        return weatherJson;
    }

    public static void main(String[] arg) {
        //new GetWeather().getweather("郑州");  //主接口函数调用执行方法
        try {
           getInstance().getweather("郑州");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}