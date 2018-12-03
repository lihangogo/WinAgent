package com.club203.service.dbService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.ibatis.session.SqlSession;

import com.club203.beans.OnlineBean;
import com.club203.mapper.OnlineMapper;
import com.club203.utils.DBTools;
import com.club203.utils.DBUtils;

public class OnlineService {

	/**
	 * 添加在线记录
	 * @param uid 用户id
	 * @return
	 */
	public boolean addOnlineRecord(Integer uid) {
		SqlSession session=DBTools.getSession();
		OnlineMapper mapper=session.getMapper(OnlineMapper.class);
		Integer i=0;
		try {
			i=mapper.selectOnlineNum();
			if(i>=100)    //在线人数不得超过100人
				return false;
			else {
				// 添加记录
				OnlineBean bean=new OnlineBean();
				bean.setId(DBUtils.getUUID());
				bean.setUid(uid);
				bean.setIpAddress(DBTools.getIpAddressByHead("10."));
				String ip_160=DBTools.getIpAddressByHead("192.168.160.");
				if(null!=ip_160)	
					bean.setIpAddress_192(ip_160);
				else
					return false;
				bean.setStartTimeStamp(DBTools.getNetworkTime());
				
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
	 * 检查在线人数是否超过100人
	 * @param uid
	 * @return
	 */
	public boolean checkOnlineNumber() {
		SqlSession session=DBTools.getSession();
		OnlineMapper mapper=session.getMapper(OnlineMapper.class);
		Integer i=0;
		try {
			i=mapper.selectOnlineNum();
			if(i>100)
				return false;
			else
				return true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 用户下线时删除该登录记录
	 * @param uid 用户id
	 * @return
	 */
	public boolean deleteOnlineRecord(Integer uid) {
		DataOutputStream dos=null;
		DataInputStream dis=null;
		Socket socket=null;
		byte[] bytes=null;
		int length=0;
		try {
			socket=new Socket();
			socket.connect(new InetSocketAddress("10.108.101.219", 33457), 10*1000);
			dos=new DataOutputStream(socket.getOutputStream());
			dos.write("logout_user".getBytes());
			dos.flush();
			dis=new DataInputStream(socket.getInputStream());
			bytes=new byte[1024];
			length=dis.read(bytes);
			String message=new String(bytes,0,length);
			if(message.equals("ok")) {
				dos.write(String.valueOf(uid).getBytes());
				dos.flush();
			}
			socket.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(dos!=null)
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(socket!=null)
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return false;
	}
	
	/**
	 * 判断该用户是否已登录
	 * @param uid
	 * @return
	 */
	public boolean isOnline(Integer uid) {
		SqlSession session=DBTools.getSession();
		OnlineMapper mapper = session.getMapper(OnlineMapper.class);
		OnlineBean bean=null;
		try {
			bean=mapper.selectOnline(uid);
			if(null==bean)
				return false;
			else
				return true;
		}catch(Exception e) {
			e.printStackTrace();
			return true;
		}
	}
	
}
