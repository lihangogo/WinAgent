package com.club203.test;

import org.apache.ibatis.session.SqlSession;

import com.club203.utils.DBTools;


public class MyTest {

	public SqlSession session;
	
	
	public void getSqlSession() {
		session=DBTools.getSession();
	}
	
}
