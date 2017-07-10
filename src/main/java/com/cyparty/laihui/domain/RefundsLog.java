package com.cyparty.laihui.domain;

import java.util.Date;

/**
 * 退款记录
 * Created by YangGuang on 2017/7/10.
 */
public class RefundsLog {

    private Integer refundsId;
    private String outTradeNo;
    private double refundsPrice;
    private String refundsTime;
    private int refundsType;
    private int userId;

    public String getRefundsTime() {
        return refundsTime;
    }

    public void setRefundsTime(String refundsTime) {
        this.refundsTime = refundsTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getRefundsId() {
        return refundsId;
    }

    public void setRefundsId(Integer refundsId) {
        this.refundsId = refundsId;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public double getRefundsPrice() {
        return refundsPrice;
    }

    public void setRefundsPrice(double refundsPrice) {
        this.refundsPrice = refundsPrice;
    }

    public int getRefundsType() {
        return refundsType;
    }

    public void setRefundsType(int refundsType) {
        this.refundsType = refundsType;
    }
}
