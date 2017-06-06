package com.cyparty.laihui.domain;

/**
 * Created by pangzhenpeng on 2017/6/3.
 */
public class Partner {
    private int id;
    private String partnerIcon;
    private String partnerIconUrl;
    private String partnerUrl;
    private String createTime;
    private int isEnable;
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPartnerIconUrl() {
        return partnerIconUrl;
    }

    public void setPartnerIconUrl(String partnerIconUrl) {
        this.partnerIconUrl = partnerIconUrl;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPartnerIcon() {
        return partnerIcon;
    }

    public void setPartnerIcon(String partnerIcon) {
        this.partnerIcon = partnerIcon;
    }

    public String getPartnerUrl() {
        return partnerUrl;
    }

    public void setPartnerUrl(String partnerUrl) {
        this.partnerUrl = partnerUrl;
    }

    public int getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(int isEnable) {
        this.isEnable = isEnable;
    }
}
