package com.mybatis;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;

public class OnlineTempService {
	
	/**
	 * 删除所有online的临时信息
	 * @return
	 */
	public boolean deleteAllOnlineTemp() {
		SqlSession sqlSession=DBTools.getSession();
		OnlineTempMapper mapper=sqlSession.getMapper(OnlineTempMapper.class);
		try {
			mapper.delAllOnlineTemp();
			sqlSession.commit();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			sqlSession.rollback();
			return false;
		}	
	}
	
	/**
	 * 获取所有online的临时信息
	 * @return
	 */
	public ArrayList<OnlineTempBean> getOnlineTempList() {
		SqlSession session=DBTools.getSession();
		OnlineTempMapper mapper = session.getMapper(OnlineTempMapper.class);
		ArrayList<OnlineTempBean> beans=new ArrayList<OnlineTempBean>();
		try {
			beans=mapper.selectAllOnlineTemp();
			return beans;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return beans;
	}
	
	/**
	 * 添加online的临时信息
	 * @param otb
	 * @return
	 */
	public boolean addOnlineTemp(OnlineTempBean otb) {
		SqlSession session=DBTools.getSession();
		OnlineTempMapper mapper = session.getMapper(OnlineTempMapper.class);
		Integer i=0;
		try {
			i=mapper.addOnlineTemp(otb);
			session.commit();
			if(i>0)
				return true;
			else 
				return false;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 添加online的临时信息
	 * @param ob
	 * @return
	 */
	public boolean addOnlineTemp(OnlineBean ob) {
		OnlineTempBean otb=new OnlineTempBean();
		otb.setId(ob.getId());
		otb.setIpAddress(ob.getIpAddress());
		otb.setIpAddress_192(ob.getIpAddress_192());
		otb.setStartTimeStamp(ob.getStartTimeStamp());
		otb.setUid(ob.getUid());
		
		SqlSession session=DBTools.getSession();
		OnlineTempMapper mapper = session.getMapper(OnlineTempMapper.class);
		Integer i=0;
		try {
			i=mapper.addOnlineTemp(otb);
			session.commit();
			if(i>0)
				return true;
			else 
				return false;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
