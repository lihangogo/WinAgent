package com.club203.service.dbService;

import java.io.FileReader;
import java.io.IOException;

import org.apache.ibatis.session.SqlSession;

import com.club203.beans.OnlineBean;
import com.club203.mapper.OnlineMapper;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.utils.DBTools;
import com.club203.utils.DBUtils;

public class OnlineService {

	/**
	 * 添加在线记录
	 * @return
	 */
	public boolean addOnlineRecord() {
		SqlSession session=DBTools.getSession();
		OnlineMapper mapper=session.getMapper(OnlineMapper.class);
		Integer i=0;
		try {
			i=mapper.selectOnlineNum();
			if(i>=100)    //在线人数不得超过100人
				return false;
			else {
				Integer uid=1;
				try {
					//读取当前登录的用户名
					FileReader fileReader=new FileReader(Openvpn.getAuthenFilepath());
					char[] buf=new char[1024];
					int num=0;
					num=fileReader.read(buf);
					String[] strs=new String(buf,0,num).split(" ");
					uid=Integer.valueOf(strs[2]);
					fileReader.close();		
				}catch(IOException e) {
					e.printStackTrace();
				}
				// 添加记录
				OnlineBean bean=new OnlineBean();
				bean.setId(DBUtils.getUUID());
				bean.setUid(uid);
				try {
					int a=mapper.addOnline(bean);
					session.commit();
					if(a>0)
						return true;
					else
						return false;
				}catch(Exception e) {
					session.rollback();
					return false;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
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
	
}
