package com.mybatis;

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
	private Integer uid;
	private Integer balance;
	private Date latestPay;
	private Integer latestPayNum;
	
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
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public Integer getBalance() {
		return balance;
	}
	public void setBalance(Integer balance) {
		this.balance = balance;
	}
	public Date getLatestPay() {
		return latestPay;
	}
	public void setLatestPay(Date latestPay) {
		this.latestPay = latestPay;
	}
	public Integer getLatestPayNum() {
		return latestPayNum;
	}
	public void setLatestPayNum(Integer latestPayNum) {
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
