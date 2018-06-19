package com.club203.mapper;

import com.club203.beans.UserBean;

public interface UserMapper {

	/**
	 * 根据用户名和密码获取User信息
	 * @param userName
	 * @param passWord
	 * @return
	 * @throws Exception
	 */
	public UserBean selectUser(String userName,String passWord)
		throws Exception;
	
}
