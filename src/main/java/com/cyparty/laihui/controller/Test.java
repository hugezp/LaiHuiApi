package com.cyparty.laihui.controller;

import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.Business;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Administrator on 2017/4/15.
 */
@Controller
public class Test {
@Autowired
    AppDB appDB;

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public void test(){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        String where = " where business_name='庞振朋' and business_mobile='15738961936'";
        List<Business> businessList = appDB.getMerchantJion(where);
    }
}
