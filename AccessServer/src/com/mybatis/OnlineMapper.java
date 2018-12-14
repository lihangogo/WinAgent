package com.mybatis;

import java.util.ArrayList;

import com.mybatis.OnlineBean;

public interface OnlineMapper {
	
	/**
	 * 用户登出时删除 本条在线记录
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public Integer delOnline(Integer uid)
		throws Exception;
	
	/**
	 * 获取所有在线账号信息
	 * @return
	 * @throws Exception
	 */
	public ArrayList<OnlineBean> selectAllOnline()
		throws Exception;
	
}
