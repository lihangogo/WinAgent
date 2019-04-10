package com;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 接入服务器运行的程序
 * @author 李瀚
 *
 */
public class Main {

	/**
	 * 主函数
	 * @param args
	 */
	public static void main(String[] args) {
		//处理客户端下线时的请求并计费
		OnlineStatusManager.manageOpt();
		//定时将当日的新登录记录添加到当日的历史记录中
		timerCheckOnlineStatus();
		//每天的凌晨一点，将前24小时的登录历史记录删除
		TimerManager.check();
		//处理客户端用于确认连接的上报消息
		new ResponseReportMessage().run();
	}

	/**
	 * 定时检查在线状况
	 */
	private static void timerCheckOnlineStatus() {
		Runnable runnable = new Runnable() {  
            public void run() {         
            	CheckOnlineStatus.check();
            }  
        };  
        ScheduledExecutorService service = Executors  
                .newSingleThreadScheduledExecutor();  
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间  
        service.scheduleAtFixedRate(runnable, 0, 60, TimeUnit.SECONDS); 
	}
}
