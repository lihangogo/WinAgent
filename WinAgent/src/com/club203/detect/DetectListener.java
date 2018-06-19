package com.club203.detect;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.club203.core.AgentPresenter;
import com.club203.dialog.MessageDialog;

/**
 * @author hehaoxing
 * 此类维护了一组监听服务，包括DNS解析与Ping检测等一系列服务
 * 监听超时则触发响应的重启代理，重启失败代理关闭
 * 超时顺序：Ping -> DNS -> Http，避免重叠
 */
public class DetectListener {
	
	private List<DetectService> detectServicesList = new LinkedList<>();
	private volatile AtomicBoolean flag = new AtomicBoolean(true); 
	private static DetectListener heartBeat = new DetectListener();
	//维护Agent的引用，在连接中断的时候通知Agent
	private AgentPresenter agentP = AgentPresenter.getAgentPresenter();
	
	private final static Logger logger = LoggerFactory.getLogger(DetectListener.class);
	
	private DetectListener() {
		super();
	}
	
	public static DetectListener getInstance() {
		return heartBeat;
	}
	
	public DetectListener init() {
		logger.info("Initializating heartbeat with no config");
		detectServicesList.clear();
		return this;
	}

	/**
	 * 添加新的监听器
	 */
	public DetectListener adddetectServices(DetectService detectService) {
		logger.info("Adding detecting service : " + detectService.getClass());
		detectServicesList.add(detectService);
		return this;
	}

	public void setDetectServicesList(List<DetectService> detectServicesList) {
		this.detectServicesList = detectServicesList;
	}

	public List<DetectService> getDetectServicesList() {
		return detectServicesList;
	}

	public void start() {
		logger.info("Starting detecting the status of connection");
		flag.set(true);
		new Thread(() -> {
			for (DetectService detectService : detectServicesList) {
				detectService.start();
			}
			checkConnect:
			while(true) {
				//有一种探测失效，则全部失效
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) { }
				for (DetectService detectService : detectServicesList) {
					//线程在没有退出时flag标志位被设置，则一定是用户点击关闭造成的
					if(!detectService.getStatus()) {
						synchronized (flag) {
							if(flag.get() == false) {
								return;
							}
							new MessageDialog(detectService.getErrorMessage()).show();
						}
						//不过无论重连与关闭的先后顺序，最后一定是关闭被正确执行
						logger.info(detectService.getClass() + " : connection failed");
						//连接断开，取消监听器线程
						for (DetectService detectSrv : detectServicesList) {
							detectSrv.cancel();
						}
						//通知业务类连接中断，重新连接
						logger.info("reconnecting proxy ...");
						agentP.reconnectProxy();
						break checkConnect;
					}
				}
			}
			flag.set(false);
		}).start();
	}
	
	public void cancel() {
		//取消心跳探测，用于用户主动关闭隧道
		logger.info("Cancelling detecting the status of connection");
		flag.set(false);
		for (DetectService detectService : detectServicesList) {
			detectService.cancel();
		}
	}
}
