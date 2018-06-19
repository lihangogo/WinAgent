package com.club203.service.test;

import org.apache.ibatis.session.SqlSession;

import com.club203.beans.UserBean;
import com.club203.mapper.UserMapper;
import com.club203.utils.DBTools;

public class Test {

	@org.junit.Test
	public void selectAllUser() {
		SqlSession session = DBTools.getSession();
		UserMapper mapper = session.getMapper(UserMapper.class);
		try {
			UserBean user = mapper.selectUser("lihan", "lh991978");
			System.out.println(user.toString());
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
		}
	}

}
