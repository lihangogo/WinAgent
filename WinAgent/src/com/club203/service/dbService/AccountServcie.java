package com.club203.service.dbService;

import org.apache.ibatis.session.SqlSession;

import com.club203.beans.AccountBean;
import com.club203.mapper.AccountMapper;
import com.club203.utils.DBTools;

public class AccountServcie {

	/**
	 * 根据用户ID查询账户信息
	 * @param uid
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

	/**
	 * 更新账户信息
	 * @param accountBean
	 * @return
	 */
	public boolean updateAccount(AccountBean accountBean) {
		SqlSession session=DBTools.getSession();
		AccountMapper mapper=session.getMapper(AccountMapper.class);
		Integer i=0;
		try {
			i=mapper.updateAccount(accountBean);
			session.commit();
			if(i>0)
				return true;
			else
				return false;
		}catch(Exception e) {
			e.printStackTrace();
			session.rollback();
			return false;
		}
	}
	
	public boolean reckonFee(long startTime,long stopTime) {
		return true;
	}
}
