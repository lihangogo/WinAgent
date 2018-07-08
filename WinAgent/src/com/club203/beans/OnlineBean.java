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
	
	@Override
	public String toString() {
		return "OnlineBean [id=" + id + ", uid=" + uid + "]";
	}
	
}
