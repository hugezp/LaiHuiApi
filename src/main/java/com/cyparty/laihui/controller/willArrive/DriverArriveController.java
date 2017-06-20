package com.cyparty.laihui.controller.willArrive;

import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.service.ArriveOrderService;
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
 * 必达单车主端
 * Created by YangGuang on 2017/6/20.
 */
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
@Controller
public class DriverArriveController {

    @Autowired
    private AppDB appDB;

    /**
     * 车主抢必达单默认生成车单
     */
    @ResponseBody
    @RequestMapping("/driver/default")
    public ResponseEntity<String> passengerAgree(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        String json = ArriveOrderService.passengerAgree(appDB,request);
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }
}
