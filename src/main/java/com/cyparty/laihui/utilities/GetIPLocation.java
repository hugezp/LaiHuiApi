package com.cyparty.laihui.utilities;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhu on 2016/12/7.
 */
public class GetIPLocation {

    public static void main(String[] args) {

        System.out.println(getIpLocation("120.52.90.88",0));
        getIpLocation("428114951474",1);

    }
    public static String getIpLocation(String keyword,int type){
        String url="http://www.baidu.com/s?ie=utf-8&wd="+keyword;

        if(type==0){
            String resultHtml=sendGet(url);
            Document doc= Jsoup.parse(resultHtml);
            Elements elements=doc.select("div.result-op");
            Element element=elements.get(0);

            String actionHtml=element.select("td span").text();
            String get_ip=actionHtml.split(":")[1];
            String location=element.select("td").text().replace(actionHtml, "");
            return location;
        }else if(type==1){
            JSONObject result=new JSONObject();
            url="https://sp0.baidu.com/9_Q4sjW91Qh3otqbppnN2DJv/pae/channel/data/asyncqury?nu=3925831301821&appid=4001";
            String resultHtml=sendGet(url);
            JSONObject dataJson=JSONObject.parseObject(resultHtml);
            JSONArray dataArray=dataJson.getJSONObject("data").getJSONObject("info").getJSONArray("context");
            JSONArray nowDataArray=new JSONArray();
            for(int i=0;i<dataArray.size();i++){
                JSONObject dataObject=dataArray.getJSONObject(i);
                String time=dataObject.getString("time")+"000";
                String desc=dataObject.getString("desc");

                String date=stampToDate(time);

                dataObject.put("time",date);

                nowDataArray.add(dataObject);
            }
            String company_name=dataJson.getJSONObject("data").getJSONObject("company").getString("fullname");
            result.put("data",nowDataArray);
            result.put("company_name",company_name);
            return result.toString();
        }else {
            return null;
        }
    }
    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("cookie", "PSTM=1487642034; BIDUPSID=2303A83F30535CEF5F61312CAA1B0A51; BDRCVFR[UR0Z8CPpxHs]=mk3SLVN4HKm; PSINO=3; H_PS_PSSID=1423_21119_22035; BAIDUID=3AD63673535C38B22C6E3154E7020C28:FG=1");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                //System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /*
    * 将时间戳转换为时间
    */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        System.out.println(LocalDate.now());
        return res;
    }
}
