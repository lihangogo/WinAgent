package com.club203.service;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.core.AgentPresenter;

/**
 * @author hehaoxing
 * 用于代理连接的断线重连
 */
public abstract class ReconnectService extends Service {

	//通知操作执行完成
	private AgentPresenter agentP = AgentPresenter.getAgentPresenter();
	//定义重连操作被执行的次数
	private static final int ReconnectCount = 3;
	
	private static final Logger logger = LoggerFactory.getLogger(ReconnectService.class);
	
	public ReconnectService(Proxy proxy) {
		super(proxy);
	}
	
	/**   
	 * 实现代理的断路重连
	 */  
	@Override
	protected Boolean doInBackground() throws Exception {
		//驳回所有重连任务
		synchronized (isStop) {
			if(isStop.get() == true) {
				logger.info("Cannot reconnect to a proxy, stopping service starts");
				return null;
			}
		}
		isReconnect.set(true);
		for(int i = 0; i < ReconnectCount; i++) {
			boolean result = reconnectService();
			//执行完每轮重连操作后检查标志位，通过触发重连失败来关闭连接
			synchronized (isStop) {
				if(isStop.get() == true) {
					isReconnect.set(false);
					logger.info("Reconnecting service fail, stopping service starts");
					return false;
				}
			}
			if(result == true) {
				isReconnect.set(false);
				return true;
			}
		}
		isReconnect.set(false);
		return false;
	}
	
	/**
	 * 实现启动服务的调用
	 */
	protected abstract boolean reconnectService() throws Exception;

	/**
	 * 断路重连后展示结果
	 */
	protected void done() {
		try {
			Boolean result = get();
			if(result == null) {
				return;
			} else if(result == true) {
				agentP.reconnectSuccess(proxy);
			} else if(result == false) {
				agentP.reconnectFail(proxy);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
