package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2017/1/3.
 * 对应数据库中    pc_campaign  活动推广
 */
public class Campaign {
    private int user_id;
    private String be_popularized_mobile; //推广人手机号

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getBe_popularized_mobile() {
        return be_popularized_mobile;
    }

    public void setBe_popularized_mobile(String be_popularized_mobile) {
        this.be_popularized_mobile = be_popularized_mobile;
    }
}
