package com.club203.service.dbService;

import org.apache.ibatis.session.SqlSession;

import com.club203.beans.UserBean;
import com.club203.mapper.UserMapper;
import com.club203.utils.DBTools;

public class UserService {

	/**
	 * 根据用户名和密码获取用户
	 */
	public UserBean selectUserByIdent(String userName,String password) {
		SqlSession session = DBTools.getSession();
		UserMapper mapper = session.getMapper(UserMapper.class);
		UserBean user=null;
		try {		
			user = mapper.selectUser(userName, password);
			session.commit();
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
			return user;
		}
	}
}
