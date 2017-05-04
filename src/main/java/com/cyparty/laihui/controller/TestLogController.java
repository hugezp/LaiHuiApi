package com.cyparty.laihui.controller;

import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.utilities.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lh2 on 2017/4/27.
 */
@Controller
@RequestMapping(value = "/api/app",method= RequestMethod.POST)
public class TestLogController {

    @Autowired
    private AppDB appDB;

    @RequestMapping(value = "/testLog",method= RequestMethod.POST)
    @ResponseBody
    public String test(){
        appDB.testLog("HelloWorld");
        return "测试成功";
    }
}
