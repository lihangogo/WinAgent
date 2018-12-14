package com.mybatis;

import java.util.ArrayList;

public interface OnlineTempMapper {

	/**
	 * 删除所有在线记录
	 * @throws Exception
	 */
	public void delAllOnlineTemp()
		throws Exception;
	
	/**
	 * 获取所有在线记录
	 * @return
	 * @throws Exception
	 */
	public ArrayList<OnlineTempBean> selectAllOnlineTemp()
		throws Exception;
	
	/**
	 * 增加在线Bean
	 * @param ot
	 * @return
	 * @throws Exception
	 */
	public Integer addOnlineTemp(OnlineTempBean ot)
		throws Exception;
	
}
