package com.club203.service.dbService;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.ibatis.session.SqlSession;
import org.w3c.dom.Document;

import com.club203.beans.AccountBean;
import com.club203.mapper.AccountMapper;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.utils.DBTools;

public class AccountService {

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
	
	/**
	 * 根据服务的开始时间和结束时间计费
	 * @param startTime 开始时间 毫秒
	 * @param stopTime 结束时间 毫秒
	 * @return
	 */
	public boolean reckonFee(long startTime,long stopTime) {
		long time1=(stopTime-startTime)/(1000*60);  //分钟数
		long time=0;
		if(time1<60)
			time=1;
		else
			time/=60;   //小时数
		int rule=0;		//收费标准
		try {
			File f=new File("./conf/Version.xml");	
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
		}
		AccountBean accountBean=new AccountBean();
		accountBean.setBalance(now);
		accountBean.setUid(uid);
		if(new AccountService().updateAccount(accountBean))
			return true;
		else
			return false;
	}
}
