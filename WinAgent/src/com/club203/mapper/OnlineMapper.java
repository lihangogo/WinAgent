package com.club203.mapper;

import com.club203.beans.OnlineBean;

public interface OnlineMapper {
	
	/**
	 * 用户登录成功后添加在线记录，用于控制同时在线人数
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public Integer addOnline(OnlineBean onlineBean)
		throws Exception;
	
	/**
	 * 用户登出时删除 本条在线记录
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public Integer delOnline(Integer uid)
		throws Exception;
	
	/**
	 * 查询当前在线人数
	 * @return
	 * @throws Exception
	 */
	public Integer selectOnlineNum()
		throws Exception;
	
	/**
	 * 查询该账号是否已登录
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public OnlineBean selectOnline(Integer uid)
		throws Exception;
}
