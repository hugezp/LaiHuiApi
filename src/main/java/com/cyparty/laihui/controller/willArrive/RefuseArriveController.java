package com.cyparty.laihui.controller.willArrive;

import com.cyparty.laihui.db.ApiDB;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.service.RefuseArriveService;
import com.cyparty.laihui.utilities.NotifyPush;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by pangzhenpeng on 2017/6/17.
 */
@Controller
@RequestMapping(value = "/api/app")
public class RefuseArriveController {

    @Autowired
    private ApiDB apiDB;
    @Autowired
    private AppDB appDB;
    @Autowired
    NotifyPush notifyPush;

    private String json = "";
    /**
     * 车主拒绝乘客必达单
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/refuse/arrive")
    public ResponseEntity<String> refuse(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json;charset=UTF-8");
        json = RefuseArriveService.getRefuseArrive(appDB,apiDB,request);
        return new ResponseEntity<>(json, httpHeaders, HttpStatus.OK);
    }

    /**
     * 车主接受(抢)乘客必达单
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/snatch/arrive")
    public ResponseEntity<String> snatch(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json;charset=UTF-8");
        json = RefuseArriveService.getSnatchArrive(appDB,apiDB,request);
        return new ResponseEntity<>(json, httpHeaders, HttpStatus.OK);
    }
}
