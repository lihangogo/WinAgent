package com.club203.beans;

import java.io.Serializable;

/**
 * 在线登记实体类
 * @author lihan
 */
public class OnlineBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private Integer uid;
	private String ipAddress;
	private String ipAddress_192;
	private long startTimeStamp;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Integer getUid() {
		return uid;
	}
	
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getIpAddress_192() {
		return ipAddress_192;
	}

	public void setIpAddress_192(String ipAddress_192) {
		this.ipAddress_192 = ipAddress_192;
	}
	
	public long getStartTimeStamp() {
		return startTimeStamp;
	}

	public void setStartTimeStamp(long startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}

	@Override
	public String toString() {
		return "OnlineBean [id=" + id + ", uid=" + uid + ", ipAddress=" + ipAddress + ", ipAddress_192=" + ipAddress_192
				+ ", startTimeStamp=" + startTimeStamp + "]";
	}
	
}
