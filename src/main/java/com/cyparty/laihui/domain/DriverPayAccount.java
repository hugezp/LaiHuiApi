package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/10/13.
 * 车主提现 pay_cash_log
 */
public class DriverPayAccount {
    private int id;
    private int user_id;
    //提现账号
    private String  pay_account;
    //创建时间
    private String  create_time;
    //最近更新
    private String  last_updated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getPay_account() {
        return pay_account;
    }

    public void setPay_account(String pay_account) {
        this.pay_account = pay_account;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }
}
