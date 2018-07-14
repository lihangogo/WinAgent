package com.club203.service.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.ibatis.session.SqlSession;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.club203.beans.AccountBean;
import com.club203.beans.OnlineBean;
import com.club203.beans.UserBean;
import com.club203.dialog.ShowMessageDlg;
import com.club203.mapper.AccountMapper;
import com.club203.mapper.OnlineMapper;
import com.club203.mapper.UserMapper;
import com.club203.service.dbService.AccountService;
import com.club203.service.dbService.OnlineService;
import com.club203.utils.DBTools;
import com.club203.utils.DBUtils;

public class Test {

	// @org.junit.Test
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

	// @org.junit.Test
	public void selectAccountByUID() {
		Integer uid = 1;
		SqlSession session = DBTools.getSession();
		AccountMapper mapper = session.getMapper(AccountMapper.class);
		AccountBean account = null;
		try {
			account = mapper.selectAccount(uid);
			System.out.println(account.toString());
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
		}
	}

	// @org.junit.Test
	public void updateAccount() {
		AccountBean accountBean = new AccountBean();
		accountBean.setAccid("1");
		accountBean.setBalance(201);
		accountBean.setLatestPay(new Date());
		accountBean.setLatestPayNum(1);
		accountBean.setUid(1);
		SqlSession session = DBTools.getSession();
		AccountMapper mapper = session.getMapper(AccountMapper.class);
		Integer i = 0;
		try {
			i = mapper.updateAccount(accountBean);
			session.commit();
			if (i > 0)
				System.out.println(true);
			else
				System.out.println(false);
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
			System.out.println(0);
		}
	}

	// @org.junit.Test
	public void getNetworkTime() {
		try {
			URL url = new URL("http://www.baidu.com");
			URLConnection conn = url.openConnection();
			conn.connect();
			long dateL = conn.getDate();
			System.out.println(dateL);
			// Date date = new Date(dateL);
			// SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
			// System.out.println(dateFormat.format(date));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("");
	}
	
	//@org.junit.Test
	public void readFile() {
		try {
			File f=new File("conf\\Fee.xml");	
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document document=builder.parse(f);
			NodeList n1=document.getElementsByTagName("rule1");
			//for(int i=0;i<n1.getLength();i++)
				System.out.println(document.getElementsByTagName("perHour").item(0).getFirstChild()
						.getNodeValue());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//@org.junit.Test
	public void reckonFee() {
		//long startTime,long stopTime
		//long time=(stopTime-startTime)/(1000*60);  //分钟数
		long time=0;
		long time1=33;
		if(time1<60)
			time=1;                 
		else
			time/=60;   //小时数
		int rule=0;		//收费标准
		try {
			File f=new File("conf\\Fee.xml");	
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document document=builder.parse(f);
			rule=Integer.valueOf(document.getElementsByTagName("perHour").item(0).getFirstChild()
						.getNodeValue());
		}catch(Exception e) {
			e.printStackTrace();
		}	
		int fee=(int) (time*rule);   //最终的费用
		int now=0,uid=1;
		/*
		try {
			FileReader fileReader=new FileReader(Openvpn.getAuthenFilepath());
			char[] buf=new char[1024];
			int num=0;
			num=fileReader.read(buf);
			String[] strs=new String(buf,0,num).split(" ");
			now=Integer.valueOf(strs[3])-fee;
			uid=Integer.valueOf(strs[2]);
			fileReader.close();
		}catch(IOException e) {
			e.printStackTrace();
		}*/
		now=191-fee;
		uid=1;
		AccountBean accountBean=new AccountBean();
		accountBean.setBalance(now);
		accountBean.setUid(uid);
		if(new AccountService().updateAccount(accountBean))
			System.out.println("OK");
		else
			System.out.println("no");
	}
	
	//@org.junit.Test
	public void selectOnlineNum() {
		SqlSession session = DBTools.getSession();
		OnlineMapper mapper = session.getMapper(OnlineMapper.class);
		Integer i=0;
		try {
			i=mapper.selectOnlineNum();
			System.out.println(i);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//@org.junit.Test
	public void addOnline() {
		OnlineBean online=new OnlineBean();
		online.setId(DBUtils.getUUID());
		online.setUid(1);
		SqlSession session = DBTools.getSession();
		OnlineMapper mapper = session.getMapper(OnlineMapper.class);
		try {
			System.out.println(mapper.addOnline(online));
			session.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//@org.junit.Test
	public void addTest() {
		OnlineService onlineService=new OnlineService();
		System.out.println(onlineService.addOnlineRecord(1));
	}
	
	//@org.junit.Test
	public void delTest() {
		System.out.println(new OnlineService().deleteOnlineRecord(1));
	}
	
	//@org.junit.Test
	public void isOnlineTest() {
		System.out.println(new OnlineService().isOnline(1));
	}
	
	@org.junit.Test
	public void uiTest() {
		new ShowMessageDlg("用户数已达上限！");
	}
}
