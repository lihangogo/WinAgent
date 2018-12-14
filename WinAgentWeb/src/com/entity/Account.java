package com.entity;

import java.util.Date;

/**
 *  Account 账目实体
 */
public class Account {
    private String accountID;  //账目ID
    private int userID;  //用户ID
    private int balance;  //账目余额
    private String latestPayTime;  //最近一次支付的时间
    private int latestPayNum;  //最近一次支付的数量

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getLatestPayTime() {
        return latestPayTime;
    }

    public void setLatestPayTime(String latestPayTime) {
        this.latestPayTime = latestPayTime;
    }

    public int getLatestPayNum() {
        return latestPayNum;
    }

    public void setLatestPayNum(int latestPayNum) {
        this.latestPayNum = latestPayNum;
    }
}
