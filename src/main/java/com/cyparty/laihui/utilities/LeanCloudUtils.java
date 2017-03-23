package com.cyparty.laihui.utilities;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;

/**
 * Created by zhu on 2016/11/5.
 */
public class LeanCloudUtils {
    public static void main(String[] args) {
        //AVOSCloud.useAVCloudUS();
        AVOSCloud.initialize("cezoXWakcIWc2wV8ClKlBF3P-gzGzoHsz","YQaNUcXNmN5cQcydwkvRoyPS","1qVSqshDWP4r1sjr9XpCP3Wn");
    }
    public static void addNotifyMessage(String mobile,JSONObject jsonObject,String type){
        //AVOSCloud.setDebugLogEnabled(true);
        AVOSCloud.initialize("cezoXWakcIWc2wV8ClKlBF3P-gzGzoHsz","YQaNUcXNmN5cQcydwkvRoyPS","1qVSqshDWP4r1sjr9XpCP3Wn");
        AVObject testObject = new AVObject("NotifyRecords");
        testObject.put("notify_mobile", mobile);
        testObject.put("json_data", jsonObject.toString());
        testObject.put("push_type", type);
        try {
            testObject.save();
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

}
