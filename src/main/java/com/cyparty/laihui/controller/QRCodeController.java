package com.cyparty.laihui.controller;

import com.cyparty.laihui.utilities.OssUtil;
import com.cyparty.laihui.utilities.ZxingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 用户二维码controller
 * Created by YangGuang on 2017/4/25.
 */
@Controller
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class QRCodeController {

    @Autowired
    OssUtil ossUtil;

    /**
     * 查询二维码
     */
    @ResponseBody
    @RequestMapping(value = "/qrcode",method = RequestMethod.POST)
    public ResponseEntity<String> qrcode(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        //设置二维码保存的参数
        int width = 300, height = 300;
        String contents = "HelloWorld111";//二维码的内容,根据具体需求做修改
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0,8);
        String qrcodePath = "F:\\qrcode\\123456.png";//二维码保存路径,根据具体需求做修改
        ZxingHandler.encode2(contents, width, height, qrcodePath);
        return null;
    }
}
