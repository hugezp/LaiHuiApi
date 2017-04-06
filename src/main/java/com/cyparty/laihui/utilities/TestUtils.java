package com.cyparty.laihui.utilities;

import com.cyparty.laihui.domain.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhu on 2016/11/28.
 */
@Service
public class TestUtils {
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
    Map<String,Integer> ipMap = new HashMap<>();
    public int test(String ip){
        if (!ipMap.containsKey(ip)){
            ipMap.put(ip,1);
        }
        if (ipMap.get(ip)>1){
            return ipMap.get(ip);
        }else{
            return 0;
        }

    }
}
