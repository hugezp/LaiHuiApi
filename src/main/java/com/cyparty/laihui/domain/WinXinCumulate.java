package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/8/25.
 */
public class WinXinCumulate {
    private int id;
    private String ref_date;
    private String user_source;
    private String new_user;//新增用户
    private String cancel_user;//取消用户
    private String cumulate_user;//累计用户

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRef_date() {
        return ref_date;
    }

    public void setRef_date(String ref_date) {
        this.ref_date = ref_date;
    }

    public String getUser_source() {
        return user_source;
    }

    public void setUser_source(String user_source) {
        this.user_source = user_source;
    }

    public String getNew_user() {
        return new_user;
    }

    public void setNew_user(String new_user) {
        this.new_user = new_user;
    }

    public String getCancel_user() {
        return cancel_user;
    }

    public void setCancel_user(String cancel_user) {
        this.cancel_user = cancel_user;
    }

    public String getCumulate_user() {
        return cumulate_user;
    }

    public void setCumulate_user(String cumulate_user) {
        this.cumulate_user = cumulate_user;
    }
}
