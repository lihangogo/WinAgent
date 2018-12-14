package com.mybatis;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;

public class OnlineService {
	
	/**
	 * 用户下线时删除该登录记录
	 * @param uid 用户id
	 * @return
	 */
	public boolean deleteOnlineRecord(Integer uid) {
		SqlSession sqlSession=DBTools.getSession();
		OnlineMapper mapper=sqlSession.getMapper(OnlineMapper.class);
		Integer i=0;
		try {
			i=mapper.delOnline(uid);
			sqlSession.commit();
			if(i>0)
				return true;
			else
				return false;
		}catch(Exception e) {
			e.printStackTrace();
			sqlSession.rollback();
			return false;
		}	
	}
	
	/**
	 * 判断该用户是否已登录
	 * @param uid
	 * @return
	 */
	public ArrayList<OnlineBean> getOnlineList() {
		SqlSession session=DBTools.getSession();
		OnlineMapper mapper = session.getMapper(OnlineMapper.class);
		ArrayList<OnlineBean> beans=new ArrayList<OnlineBean>();
		try {
			beans=mapper.selectAllOnline();
			return beans;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return beans;
	}
	
}
