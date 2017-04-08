package com.cyparty.laihui.domain;

/**
 * Created by Administrator on 2017/4/7.
 */
public class TestController {
    private int _id=0;
    private String create_time;
    private String login_ip;
    private String controller;
    private String my_mobile;
    private String mobile;

    public String getMy_mobile() {
        return my_mobile;
    }

    public void setMy_mobile(String my_mobile) {
        this.my_mobile = my_mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getController() {
        return controller;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getLogin_ip() {
        return login_ip;
    }

    public void setLogin_ip(String login_ip) {
        this.login_ip = login_ip;
    }

    public void setController(String controller) {
        this.controller = controller;
    }



    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }


}
