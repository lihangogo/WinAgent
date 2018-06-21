package com.club203.mapper;

import com.club203.beans.AccountBean;

public interface AccountMapper {

	/**
	 * 根据用户的ID查询账户余额
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public AccountBean selectAccount(Integer id)
		throws Exception;
	
	/**
	 * 更新用户的账户信息
	 * @param accountBean
	 * @return
	 * @throws Exception
	 */
	public Integer updateAccount(AccountBean accountBean)
		throws Exception;
	
}
