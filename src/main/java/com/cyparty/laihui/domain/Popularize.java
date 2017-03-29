package com.cyparty.laihui.domain;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class Popularize {
     int id;
     int popularize_id;
     int popularize_parent_id;
     String popularize_parents_id;
     String popularize_code;
     String create_time;
     String update_time;
     int is_enable;
     int level;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPopularize_id() {
        return popularize_id;
    }

    public void setPopularize_id(int popularize_id) {
        this.popularize_id = popularize_id;
    }

    public int getPopularize_parent_id() {
        return popularize_parent_id;
    }

    public void setPopularize_parent_id(int popularize_parent_id) {
        this.popularize_parent_id = popularize_parent_id;
    }

    public String getPopularize_parents_id() {
        return popularize_parents_id;
    }

    public void setPopularize_parents_id(String popularize_parents_id) {this.popularize_parents_id = popularize_parents_id;}

    public String getPopularize_code() {
        return popularize_code;
    }

    public void setPopularize_code(String popularize_code) {
        this.popularize_code = popularize_code;
    }

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

    public int getIs_enable() {
        return is_enable;
    }

    public void setIs_enable(int is_enable) {
        this.is_enable = is_enable;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
