package com.cyparty.laihui.controller.willArrive;

import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.service.ArriveCarStatusService;
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
 * 必达模块车单状态
 * Created by YangGuang on 2017/6/19.
 */
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
@Controller
public class ArriveCarStatusController {

    @Autowired
    private AppDB appDB;

    /**
     * 乘客端车单状态1
     */
    @ResponseBody
    @RequestMapping(value = "/passenger/status1")
    public ResponseEntity<String> passengerStatus1(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        String json = ArriveCarStatusService.passengerStatus(request,appDB);
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }
}
