package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/9/13.
 * a dvier tisement  广告
 */
public class Adviertisement {
    private int ad_id;
    private int dest_id;
    private int total;
    private int total_weight;
    private String dest_name;
    private int ad_weight;
    private String ad_pic_url;
    private String ad_link;
    private String ad_create_time;
    private String ad_updated_time;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal_weight() {
        return total_weight;
    }

    public void setTotal_weight(int total_weight) {
        this.total_weight = total_weight;
    }

    public int getAd_id() {
        return ad_id;
    }

    public void setAd_id(int ad_id) {
        this.ad_id = ad_id;
    }

    public int getDest_id() {
        return dest_id;
    }

    public void setDest_id(int dest_id) {
        this.dest_id = dest_id;
    }

    public String getDest_name() {
        return dest_name;
    }

    public void setDest_name(String dest_name) {
        this.dest_name = dest_name;
    }

    public int getAd_weight() {
        return ad_weight;
    }

    public void setAd_weight(int ad_weight) {
        this.ad_weight = ad_weight;
    }

    public String getAd_pic_url() {
        return ad_pic_url;
    }

    public void setAd_pic_url(String ad_pic_url) {
        this.ad_pic_url = ad_pic_url;
    }

    public String getAd_link() {
        return ad_link;
    }

    public void setAd_link(String ad_link) {
        this.ad_link = ad_link;
    }

    public String getAd_create_time() {
        return ad_create_time;
    }

    public void setAd_create_time(String ad_create_time) {
        this.ad_create_time = ad_create_time;
    }

    public String getAd_updated_time() {
        return ad_updated_time;
    }

    public void setAd_updated_time(String ad_updated_time) {
        this.ad_updated_time = ad_updated_time;
    }
}
