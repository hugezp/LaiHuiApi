package com.cyparty.laihui.controller.willArrive;

import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.service.PassengerArriveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


/**
 * Created by pangzhenpeng on 2017/6/20.
 */
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
@Controller
public class PassengerArriveController {

    @Autowired
    AppDB appDB;
    private String json = "";
    @ResponseBody
    @RequestMapping(value = "/invite/driver",method = RequestMethod.POST)
    public ResponseEntity<String> inviteDriver(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        json = PassengerArriveService.getInviteDriver(request,appDB);
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }

    /**
     * 乘客订单详情
     */
    @ResponseBody
    @RequestMapping("passenger/orderDetail")
    public ResponseEntity<String> orderDetail(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        json = PassengerArriveService.orderDetail(appDB,request);
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }

}
