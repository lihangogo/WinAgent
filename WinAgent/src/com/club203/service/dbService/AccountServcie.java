package com.club203.service.dbService;

import org.apache.ibatis.session.SqlSession;

import com.club203.beans.AccountBean;
import com.club203.mapper.AccountMapper;
import com.club203.utils.DBTools;

public class AccountServcie {

	/**
	 * 根据用户ID查询账户信息
	 * @return
	 */
	public AccountBean selectAccountByUID(Integer uid) {
		SqlSession session = DBTools.getSession();
		AccountMapper mapper = session.getMapper(AccountMapper.class);
		AccountBean account = null;
		try {
			account = mapper.selectAccount(uid);
			session.commit();
			return account;
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
			return account;
		}
	}
}
