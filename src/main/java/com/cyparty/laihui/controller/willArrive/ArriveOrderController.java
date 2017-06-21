package com.cyparty.laihui.controller.willArrive;

import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.service.ArriveOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 必达单订单相关的controller
 * Created by YangGuang on 2017/6/17.
 */
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
@Controller
public class ArriveOrderController {

    @Autowired
    private AppDB appDB;

    /**
     * 乘客同意车主抢单
     */
    @Transactional
    @ResponseBody
    @RequestMapping(value = "/passenger/agree")
    public ResponseEntity<String> passengerAgree(HttpServletRequest request) throws RuntimeException{
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        String json = ArriveOrderService.passengerAgree(appDB, request);
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }

    /**
     * 乘客拒绝车主抢单
     */
    @Transactional
    @ResponseBody
    @RequestMapping(value = "/passenger/refuse")
    public ResponseEntity<String> passengerRefuse(HttpServletRequest request) throws RuntimeException{
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        String json = ArriveOrderService.passengerRefuse(appDB, request);
        return new ResponseEntity<>(json, responseHeaders, HttpStatus.OK);
    }
}
