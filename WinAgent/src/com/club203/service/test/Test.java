package com.club203.service.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.ibatis.session.SqlSession;

import com.club203.beans.AccountBean;
import com.club203.beans.UserBean;
import com.club203.mapper.AccountMapper;
import com.club203.mapper.UserMapper;
import com.club203.utils.DBTools;

public class Test {

	//@org.junit.Test
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
	
	//@org.junit.Test
	public void selectAccountByUID() {
		Integer uid=1;
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
	
	//@org.junit.Test
	public void updateAccount() {
		AccountBean accountBean=new AccountBean();
		accountBean.setAccid("1");
		accountBean.setBalance(201);
		accountBean.setLatestPay(new Date());
		accountBean.setLatestPayNum(1);
		accountBean.setUid(1);
		SqlSession session=DBTools.getSession();
		AccountMapper mapper=session.getMapper(AccountMapper.class);
		Integer i=0;
		try {
			i=mapper.updateAccount(accountBean);
			session.commit();
			if(i>0)
				System.out.println(true);
			else
				System.out.println(false);
		}catch(Exception e) {
			e.printStackTrace();
			session.rollback();
			System.out.println(0);
		}
	}
	
	@org.junit.Test
	public void getNetworkTime() {
		try {
            URL url = new URL("http://www.baidu.com");
            URLConnection conn = url.openConnection();
            conn.connect();
            long dateL = conn.getDate();
            System.out.println(dateL);
            //Date date = new Date(dateL);
            //SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            //System.out.println(dateFormat.format(date));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("");
	}
}
