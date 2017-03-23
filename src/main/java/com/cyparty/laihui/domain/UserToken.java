package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/10/8.
 *  对应数据库中的 pc_user_token
 */
public class UserToken {
    private int user_id;
    private String  token;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
