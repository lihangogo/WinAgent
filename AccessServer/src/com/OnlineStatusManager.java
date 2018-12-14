package com;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.mybatis.AccountBean;
import com.mybatis.AccountService;
import com.mybatis.OnlineBean;
import com.mybatis.OnlineService;

public class OnlineStatusManager {
	
	public static void manageOpt() {
		try {
			final ServerSocket server = new ServerSocket(33457);
			Thread th = new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {					
							Socket socket = server.accept();
							handleRequest(socket);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			th.run(); //线程运行
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 处理客户端请求   (登出-  ) 
	 * @param socket
	 */
	private static void handleRequest(Socket socket) {
		DataInputStream dis=null;
		DataOutputStream dos=null;
		byte[] inputByte=null;
		int length=0;
		String message=null;
		try {
			dis=new DataInputStream(socket.getInputStream());
			inputByte=new byte[1024];
			length=dis.read(inputByte);
			message=new String(inputByte,0,length);
			System.out.println(message);
			if("logout_user".equals(message)){
				inputByte="ok".getBytes();
				dos=new DataOutputStream(socket.getOutputStream());
				dos.write(inputByte,0,inputByte.length);
				dos.flush();
				
				inputByte=new byte[1024];
				length=dis.read(inputByte);
				message=new String(inputByte,0,length);
				Integer uid=Integer.valueOf(message);
				changeAccount(uid);
				if(new OnlineService().deleteOnlineRecord(uid))
					System.out.println("下线成功: "+message);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算使用时间
	 * @param uid
	 */
	private static void changeAccount(Integer uid) {
		OnlineService os=new OnlineService();
		Long startTime=0L;
		for(OnlineBean t:os.getOnlineList()) {
			if(t.getUid().intValue()==uid.intValue()) {
				startTime=t.getStartTimeStamp();
				break;
			}
		}
		updateAccount(uid, computeFee(startTime));
	}
	
	/**
	 * 根据使用时间扣费
	 * @param uid
	 * @param sum
	 */
	private static void updateAccount(int uid,int sum) {
		AccountService accountService=new AccountService();
		AccountBean goal=accountService.selectAccountByUID(uid);
		goal.setBalance(goal.getBalance()-sum);
		accountService.updateAccount(goal);
	}
	
	/**
	 * 计算费用
	 * @param start
	 * @return
	 */
	private static int computeFee(Long start) {
		long minutes=(System.currentTimeMillis()-start)/(1000*60); //分钟数
		float rule=0.5f;
		try {
			File f=new File("conf/Version.xml");	
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document document=builder.parse(f);
			rule=Float.valueOf(document.getElementsByTagName("perMinute").item(0).getFirstChild()
						.getNodeValue());
			return (int)(minutes*rule);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return (int)(minutes*rule);
	}
}
