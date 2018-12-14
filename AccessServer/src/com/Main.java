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
		OnlineStatusManager.manageOpt();
		timerCheckOnlineStatus();
		TimerManager.check();
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
