package com.cyparty.laihui.domain;

/**
 * Created by zhu on 2016/11/5.
 *   对应数据库中 pc_carousel 轮播图
 */
public class Carousel {
    private int _id;
    private int seq;//轮播顺序
    private String  image_url;
    private String  image_link;// 引用连接
    private String  image_title;
    private String  image_subtitle;//副标题
    private String  create_time;
    private String  update_time;

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getImage_title() {
        return image_title;
    }

    public void setImage_title(String image_title) {
        this.image_title = image_title;
    }

    public String getImage_subtitle() {return image_subtitle;}

    public void setImage_subtitle(String image_subtitle) {this.image_subtitle = image_subtitle;}
}
