package com.entity;

import java.util.Date;

/**
 *  PayRecord 支付记录实体
 */
public class PayRecord {
    private String payRecordID;  //支付记录ID
    private int userID;  //用户ID
    private String payTime;  //支付时间
    private int payNum;  //支付数量

    public String getPayRecordID() {
        return payRecordID;
    }

    public void setPayRecordID(String payRecordID) {
        this.payRecordID = payRecordID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public int getPayNum() {
        return payNum;
    }

    public void setPayNum(int payNum) {
        this.payNum = payNum;
    }
}
