package com.cyparty.laihui.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.domain.Carousel;
import com.cyparty.laihui.utilities.OssUtil;
import com.cyparty.laihui.utilities.ReturnJsonUtil;
import com.cyparty.laihui.utilities.Utils;
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
 * app广告接口
 * Created YangGuang on 2017/4/25.
 */
@Controller
@RequestMapping(value = "/api/app", method = RequestMethod.POST)
public class CarouselController {
    
    @Autowired
    private AppDB appDB;

    @Autowired
    OssUtil ossUtil;

    /**
     * 闪屏页接口
     */
    @ResponseBody
    @RequestMapping(value = "/splash_screen", method = RequestMethod.POST)
    public ResponseEntity<String> splashScreen(HttpServletRequest request){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        String json = "";
        try {
            String action=request.getParameter("action");
            boolean is_success=false;
            int page=0;
            int size=10;
            if(request.getParameter("page")!=null&&!request.getParameter("page").trim().equals("")){
                try {
                    page=Integer.parseInt(request.getParameter("page"));
                } catch (NumberFormatException e) {
                    page=0;
                    e.printStackTrace();
                }
            }
            if(request.getParameter("size")!=null&&!request.getParameter("size").trim().equals("")){
                try {
                    size=Integer.parseInt(request.getParameter("size"));
                } catch (NumberFormatException e) {
                    size=10;
                    e.printStackTrace();
                }
            }
            switch (action) {
                case "add":
                    String title=request.getParameter("title");
                    String link=request.getParameter("link");
                    String seq=request.getParameter("weight");

                    Carousel carousel=new Carousel();

                    carousel.setImage_link(link);
                    carousel.setImage_title(title);
                    int now_seq=0;
                    if(seq!=null&&!seq.isEmpty()){
                        now_seq=Integer.parseInt(seq);
                    }
                    carousel.setSeq(now_seq);
                    //判断是更新还是创建
                    String ad_id=request.getParameter("id");
                    int id=0;
                    if(ad_id!=null&&!ad_id.trim().equals("")){
                        id=Integer.parseInt(ad_id);
                    }
                    if(id>0){
                        //更新
                        String filePath = Utils.fileImgUpload("img", request);
                        if (filePath != null && !filePath.trim().equals("")) {
                            String image_local = filePath.substring(filePath.indexOf("upload"));
                            String arr[] = image_local.split("\\\\");
                            String image_oss = arr[arr.length - 1];
                            try {
                                if (ossUtil.uploadFileWithResult(request, image_oss, image_local)) {
                                    image_oss = "https://laihuipincheoss.oss-cn-qingdao.aliyuncs.com/" + image_oss;
                                    carousel.setImage_url(image_oss);
                                }
                            } catch (Exception e) {
                                image_oss = null;
                                carousel.setImage_url(image_oss);
                            }
                        }else {
                            carousel.setImage_url(null);
                        }
                        String update_where="";
                        if(carousel.getImage_url()!=null){
                            //update
                            update_where=" set pc_image_url='"+carousel.getImage_url()+"',pc_image_link='"+carousel.getImage_link()+"',pc_image_title='"+title+"',pc_image_seq="+carousel.getSeq()+",pc_image_update_time='"+Utils.getCurrentTime()+"' where _id="+id;
                        }else {
                            update_where=" set pc_image_link='"+carousel.getImage_link()+"',pc_image_title='"+title+"',pc_image_seq="+carousel.getSeq()+",pc_image_update_time='"+Utils.getCurrentTime()+"' where _id="+id;
                        }

                        appDB.update("pc_carousel",update_where);
                        json = ReturnJsonUtil.returnSuccessJsonString(result, "创建成功！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }else {
                        //新建
                        String filePath = Utils.fileImgUpload("img", request);
                        if (filePath != null && !filePath.trim().equals("")) {
                            String image_local = filePath.substring(filePath.indexOf("upload"));
                            String arr[] = image_local.split("\\\\");
                            String image_oss = arr[arr.length - 1];
                            try {
                                if (ossUtil.uploadFileWithResult(request, image_oss, image_local)) {
                                    image_oss = "https://laihuipincheoss.oss-cn-qingdao.aliyuncs.com/" + image_oss;
                                    carousel.setImage_url(image_oss);
                                }
                            } catch (Exception e) {
                                image_oss = null;
                                carousel.setImage_url(image_oss);
                            }
                        }else {
                            carousel.setImage_url(null);
                        }
                        if(carousel.getImage_url()!=null&&carousel.getSeq()!=0){
                            carousel.setType(2);
                            is_success=appDB.createCarousel(carousel);
                        }
                        if(is_success){
                            json = ReturnJsonUtil.returnSuccessJsonString(result, "创建成功！");
                            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                        }
                    }
                    json = ReturnJsonUtil.returnFailJsonString(result, "创建失败！");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                case "show":
                    if(request.getParameter("id")!=null&&!request.getParameter("id").isEmpty()){
                        id=Integer.parseInt(request.getParameter("id"));
                    }else {
                        id=0;
                    }
                    json = ReturnJsonUtil.returnSuccessJsonString(ReturnJsonUtil.getCarouselJson(appDB, page, size, id,2), "轮播图信息获取成功");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                case "update":
                    if(request.getParameter("id")!=null&&!request.getParameter("id").isEmpty()){
                        id=Integer.parseInt(request.getParameter("id"));
                    }else {
                        id=0;
                    }
                    now_seq=1;
                    if(request.getParameter("seq")!=null&&!request.getParameter("seq").isEmpty()){
                        now_seq=Integer.parseInt(request.getParameter("seq"));
                    }
                    if(id>0){
                        String update_sql=" set pc_image_seq="+now_seq+" where _id="+id;
                        appDB.update("pc_carousel",update_sql);
                        json = ReturnJsonUtil.returnSuccessJsonString(result, "弹出广告权重更改成功");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
                    json = ReturnJsonUtil.returnFailJsonString(result, "弹出广告权重更改失败");
                    return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                case "delete":
                    if(request.getParameter("id")!=null&&!request.getParameter("id").isEmpty()){
                        id=Integer.parseInt(request.getParameter("id"));
                    }else {
                        id=0;
                    }
                    String delete_sql=" where _id="+id;
                    is_success=appDB.delete("pc_carousel",delete_sql);
                    if(is_success){
                        json = ReturnJsonUtil.returnSuccessJsonString(result, "弹出广告删除成功！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }else {
                        json = ReturnJsonUtil.returnFailJsonString(result, "弹出广告删除失败！");
                        return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
                    }
            }
            json = ReturnJsonUtil.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            json = ReturnJsonUtil.returnFailJsonString(result, "获取参数错误");
            return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
        }
    }
}
