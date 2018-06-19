package com.club203.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * PayRecord充值记录 实体类
 * @author lihan
 *
 */
public class PayRecordBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;

	private String pid;
	private int uid;
	private Date dateTime;
	private int payNum;
	
	public PayRecordBean() {
		super();
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public int getPayNum() {
		return payNum;
	}
	public void setPayNum(int payNum) {
		this.payNum = payNum;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "PayRecordBean [pid=" + pid + ", uid=" + uid + ", dateTime=" + dateTime + ", payNum=" + payNum + "]";
	}
	
}
