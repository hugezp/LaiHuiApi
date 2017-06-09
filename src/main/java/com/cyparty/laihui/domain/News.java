package com.cyparty.laihui.domain;

import com.alibaba.fastjson.JSONObject;

public class News {
    //pc_news×Ö¶Î
    private Integer id;

    private String title;

    private String description;

    private String createTime;

    private Integer isDel;

    private String updateTime;

    private String publisher;

    private String content;

    private int newsType;

    private String image;

    //pc_new_type±í×Ö¶Î
    private int typeId;

    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    private int isEnable;


    public Integer getIsDel() {
        return isDel;
    }

    public void setIsDel(Integer isDel) {
        this.isDel = isDel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime == null ? null : createTime.trim();
    }


    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime == null ? null : updateTime.trim();
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher == null ? null : publisher.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public int getNewsType() {
        return newsType;
    }

    public void setNewsType(int newsType) {
        this.newsType = newsType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(int isEnable) {
        this.isEnable = isEnable;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public JSONObject ModelToJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",this.getId());
        jsonObject.put("title",this.getTitle());
        jsonObject.put("description",this.getDescription());
        jsonObject.put("publisher",this.getPublisher());
        jsonObject.put("content",this.getContent());
        jsonObject.put("createTime",this.getCreateTime());

        return jsonObject;
    }
}