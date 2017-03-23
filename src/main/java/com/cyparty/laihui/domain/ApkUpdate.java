package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/9/14.
 * 对应数据库 pc_apk_updated
 */
public class ApkUpdate {
    private int id;
    private String download_url;//下载路径
    private int versionCode;//版本号
    private String updateMessage;//更新消息
    private String create_time;//版本更新时间
    private int  is_must;//是否必须更新
    private String  soucre;//区分ios 或者android

    public String getSoucre() {
        return soucre;
    }

    public void setSoucre(String soucre) {
        this.soucre = soucre;
    }

    public int getIs_must() {
        return is_must;
    }

    public void setIs_must(int is_must) {
        this.is_must = is_must;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
