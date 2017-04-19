package com.cyparty.laihui.utilities;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by zhu on 2016/11/10.
 */
@Service
public class NotifyPush {
    public static void main(String[] args) {
        //pinCheNotify("d6a5018284f8e0ffd4d2789a84d5c884");
        //pinCheNotify("24","18638016939","李先生",28);
        /*pinCheNotify("12","18338228688","王女士",28);
        pinCheNotify("14","18338228688","王女士",28);
        pinCheNotify("20","18338228688","王女士",28);
        pinCheNotify("21","18338228688","王女士",28);
        pinCheNotify("22","18338228688","王女士",28);
        pinCheNotify("24","18338228688","王女士",28);*/
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("push_time",Utils.getCurrentTime());
        jsonObject.put("content","测试内容");
        pinCheNotify("11","15738961936","测试推送",28,jsonObject,Utils.getCurrentTime());
    }
    @Async
    public static JSONObject pinCheNotify(String type,String mobile,String content,int id,JSONObject pushObject,String push_time)  {

        JSONObject dataObject = new JSONObject();
        JSONObject  result=new JSONObject();
        int i=0;
        String url = "https://leancloud.cn/1.1/push";
        String json="";
        String forword="";
        dataObject.put("title","来回拼车");
        dataObject.put("badge","Increment");
        dataObject.put("action","com.laihui.pinche.push");
        switch (type){
            case "11":
                //司机抢单
                dataObject.put("alert",content);
                dataObject.put("notify_type",type);
                dataObject.put("id",id);
                dataObject.put("badge","Increment");
                dataObject.put("sound",type+".caf");
                pushObject.put("push_time",push_time);
                pushObject.put("content",content);
                break;
            case "12":
                //司机取消抢单，乘客未支付
                dataObject.put("alert",content);
                dataObject.put("notify_type",type);
                dataObject.put("id",id);
                dataObject.put("badge","Increment");
                dataObject.put("sound",type+".caf");
                pushObject.put("push_time",push_time);
                pushObject.put("content",content);
                break;
            case "21":
                //乘客同意抢单
                dataObject.put("alert",content);
                dataObject.put("notify_type",type);
                dataObject.put("id",id);
                dataObject.put("badge","Increment");
                dataObject.put("sound",type+".caf");
                pushObject.put("push_time",push_time);
                pushObject.put("content",content);
                break;
            case "22":
                //乘客拒绝抢单
                dataObject.put("alert",content);
                dataObject.put("notify_type",type);
                dataObject.put("id",id);
                dataObject.put("badge","Increment");
                dataObject.put("sound",type+".caf");
                pushObject.put("push_time",push_time);
                pushObject.put("content",content);
                break;
            case "23":
                //乘客取消订单
                dataObject.put("alert",content);
                dataObject.put("notify_type",type);
                dataObject.put("id",id);
                dataObject.put("badge","Increment");
                dataObject.put("sound",type+".caf");
                pushObject.put("push_time",push_time);
                pushObject.put("content",content);
                break;
            case "24":
                //乘客确认到达
                dataObject.put("alert",content);
                dataObject.put("notify_type",type);
                dataObject.put("id",id);
                dataObject.put("badge","Increment");
                dataObject.put("sound",type+".caf");
                pushObject.put("push_time",push_time);
                pushObject.put("content",content);
                break;
            case "25":
                //乘客发布推送同线路司机
                dataObject.put("alert",content);
                dataObject.put("notify_type",type);
                dataObject.put("id",id);
                dataObject.put("badge","Increment");
                dataObject.put("sound",type+".caf");
                pushObject.put("content",content);
                pushObject.put("push_time",push_time);
                break;
            case "26":
                //乘客支付完成通知司机
                dataObject.put("alert",content);
                dataObject.put("notify_type",type);
                dataObject.put("id",id);
                dataObject.put("badge","Increment");
                dataObject.put("sound",type+".caf");
                pushObject.put("content",content);
                pushObject.put("push_time",push_time);
                break;
            case "27":
                //乘客通知司机发车
                dataObject.put("alert",content);
                dataObject.put("notify_type",type);
                //订单id
                dataObject.put("id",id);
                dataObject.put("badge","Increment");
                dataObject.put("sound",type+".caf");
                pushObject.put("content",content);
                pushObject.put("push_time",push_time);
                break;
            case "28":
                //乘客邀请车主抢单
                dataObject.put("alert",content);
                dataObject.put("notify_type",type);
                //订单id
                dataObject.put("id",id);
                dataObject.put("badge","Increment");
                dataObject.put("sound",type+".caf");
                pushObject.put("content",content);
                pushObject.put("push_time",push_time);
                break;
            default:
                break;
        }
        //把自定义数据添加进去
        dataObject.put("push",pushObject);

        JSONObject sendJson=new JSONObject();
        sendJson.put("cql","select * from _Installation where notify_mobile='"+mobile+"'");
        sendJson.put("prod","dev");
        sendJson.put("data",dataObject);
        json=sendJson.toJSONString();
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        StringEntity postingString = new StringEntity(json,"utf-8");// json传递
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        post.setHeader("X-LC-Id", "cezoXWakcIWc2wV8ClKlBF3P-gzGzoHsz");
        post.setHeader("X-LC-Key", "YQaNUcXNmN5cQcydwkvRoyPS");
        HttpResponse response = null;
        try {
            response = httpClient.execute(post);
            String get_content = EntityUtils.toString(response.getEntity());
            result=JSONObject.parseObject(get_content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //保存推送内容
        LeanCloudUtils.addNotifyMessage(mobile,dataObject,type);
        return result;
    }
    @Async
    public static JSONObject pinCheNotifies(String type,String mobile,String content,int id,JSONObject pushObject,String push_time)  {

        JSONObject dataObject = new JSONObject();
        JSONObject  result=new JSONObject();
        int i=0;
        String url = "https://leancloud.cn/1.1/push";
        String json="";
        String forword="";
        dataObject.put("title","来回拼车");
        dataObject.put("badge","Increment");
        dataObject.put("action","com.laihui.pinche.push");
        //把自定义数据添加进去
        dataObject.put("alert",content);
        dataObject.put("notify_type",type);
        dataObject.put("id",id);
        dataObject.put("badge","1");
        dataObject.put("sound",type+".caf");
        pushObject.put("push_time",push_time);
        pushObject.put("content",content);
        dataObject.put("push",pushObject);

        JSONObject sendJson=new JSONObject();
        sendJson.put("cql","select * from _Installation where notify_mobile='"+mobile+"'");
        sendJson.put("prod","dev");
        sendJson.put("data",dataObject);
        json=sendJson.toJSONString();
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        StringEntity postingString = new StringEntity(json,"utf-8");// json传递
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        post.setHeader("X-LC-Id", "cezoXWakcIWc2wV8ClKlBF3P-gzGzoHsz");
        post.setHeader("X-LC-Key", "YQaNUcXNmN5cQcydwkvRoyPS");
        HttpResponse response = null;
        try {
            response = httpClient.execute(post);
            String get_content = EntityUtils.toString(response.getEntity());
            result=JSONObject.parseObject(get_content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //保存推送内容
        LeanCloudUtils.addNotifyMessage(mobile,dataObject,type);
        return result;
    }
    @Async
    public void testAsyncMethod(){
        try {
            //让程序暂停100秒，相当于执行一个很耗时的任务
            System.out.println("Starting:我开始执行了！");
            Thread.sleep(100000);
            System.out.println("Ending:我执行结束了！");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
