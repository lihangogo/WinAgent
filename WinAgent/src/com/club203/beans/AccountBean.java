package com.club203.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * Account账户 实体类
 * @author lihan
 *
 */
public class AccountBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	private String accid;
	private int uid;
	private int balance;
	private Date latestPay;
	private int latestPayNum;
	
	public AccountBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getAccid() {
		return accid;
	}
	public void setAccid(String accid) {
		this.accid = accid;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public Date getLatestPay() {
		return latestPay;
	}
	public void setLatestPay(Date latestPay) {
		this.latestPay = latestPay;
	}
	public int getLatestPayNum() {
		return latestPayNum;
	}
	public void setLatestPayNum(int latestPayNum) {
		this.latestPayNum = latestPayNum;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "AccountBean [accid=" + accid + ", uid=" + uid + ", balance=" + balance + ", latestPay=" + latestPay
				+ ", latestPayNum=" + latestPayNum + "]";
	}

}
