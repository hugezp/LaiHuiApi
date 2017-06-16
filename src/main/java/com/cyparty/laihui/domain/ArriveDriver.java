package com.cyparty.laihui.domain;

import java.util.Date;

/**
 * 必达单车主实体类
 * Created by YangGuang on 2017/6/16.
 */
public class ArriveDriver {

    private Integer rId;//主键
    private String orderNo;//订单编号
    private String driverPhone;//车主电话
    private int passengerId;//乘客id
    private String createTime;//创建时间
    private int isDel;//删除标记 0删除 1正常

    public Integer getrId() {
        return rId;
    }

    public void setrId(Integer rId) {
        this.rId = rId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getIsDel() {
        return isDel;
    }

    public void setIsDel(int isDel) {
        this.isDel = isDel;
    }
}
