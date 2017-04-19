package com.cyparty.laihui.utilities;

/**
 * Created by Administrator on 2017/4/18.
 */
public class TestConfigUtils {
    public static boolean getMobile(String mobile){
        boolean is_success = false;
        switch (mobile){
            case "13298172885" :
               is_success = true;
               break;
            case "15639356022" :
                is_success = true;
                break;
            case "18538191908" :
                is_success = true;
                break;
            case "18560459018" :
                is_success = true;
                break;
            case "15516015893" :
                is_success = true;
                break;
        }
        return is_success;
    }
}
