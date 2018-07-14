package com.club203.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * User实体类
 * @author lihan
 *
 */
public class UserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer uid;
	private String userName;
	private String nickName;
	private String passWord;
	private String phone;
	private String email;
	private Date regTime;
	private Date updateTime;
	
	public UserBean() {
		super();
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getRegTime() {
		return regTime;
	}
	public void setRegTime(Date regTime) {
		this.regTime = regTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "UserBean [uid=" + uid + ", userName=" + userName + ", nickName=" + nickName + ", passWord=" + passWord
				+ ", phone=" + phone + ", email=" + email + ", regTime=" + regTime + ", updateTime=" + updateTime + "]";
	
	}
}
