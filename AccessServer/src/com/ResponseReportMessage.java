package com;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mybatis.OnlineService;
import com.tools.TimeStamp;


/**
 * 响应上报消息
 * @author club203LH
 *
 */
public class ResponseReportMessage {

	/**
	 * 维护的上报消息
	 */
	private static Map<Integer,Long> infor=null;
	
	/**
	 * 外部调用接口
	 */
	public void run() {
		infor=new ConcurrentHashMap<Integer,Long>();

		ScheduledExecutorService service=Executors
				.newSingleThreadScheduledExecutor();
		//定时两分钟执行一次
		service.scheduleAtFixedRate(getOnlineStatusListener(), 0, 
				120, TimeUnit.SECONDS);
	}
	
	/**
	 * 在线状态监听器线程的生成器
	 * @return
	 */
	private OnlineStatusListener getOnlineStatusListener() {
		return new OnlineStatusListener();
	}
	
	/**
	 * 用于定时检测在线状态的线程类(内部类)
	 * @author club203LH
	 *
	 */
	class OnlineStatusListener implements Runnable{
		
		@Override
		public void run() {
			Long timeStampNow=TimeStamp.getTimerStamp();
			for(Integer uid:infor.keySet()) {
				Long timeStampLast=infor.get(uid);
				if(((timeStampNow-timeStampLast)/1000)>120) {
					infor.remove(uid);
					System.out.println("delete "+uid);
					new OnlineService().deleteOnlineRecord(uid);
				}
			}
		}
	}
	
	/**
	 * 处理客户端上报的信息
	 * @param socket
	 */
	public static void handleReport(Socket socket,String message) {
		try {
			String strs[]=message.split(" ");
			if(!strs[0].equals("hello"))
				return;
			Integer uid=Integer.valueOf(strs[1]);
			infor.put(uid, TimeStamp.getTimerStamp());		
			System.out.println(uid+" "+TimeStamp.getTimerStamp());
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(socket!=null)
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
}
