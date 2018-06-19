package com.club203.service;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.core.AgentPresenter;
/**
 * @author hehaoxing
 * 用于关闭代理连接
 */
public abstract class StopService extends Service {

	//通知操作执行完成
	private AgentPresenter agentP = AgentPresenter.getAgentPresenter();
		
	private final static Logger logger = LoggerFactory.getLogger(StopService.class);
	
	public StopService(Proxy proxy) {
		super(proxy);
	}
	
	/**   
	 * 实现代理的关闭
	 */
	@Override
	protected Boolean doInBackground() throws Exception {
		//触发重连则不执行退出操作，而是强制重连操作产生false触发退出
		isStop.set(false);
		synchronized (isReconnect) {
			if(isReconnect.get() == true) {
				isStop.set(true);
				logger.info("Stopping service starts, cancel reconnecting service");
				return null;
			}
		}
		synchronized (isStart) {
			if(isStart.get() == true) {
				isStop.set(true);
				logger.info("Stopping service starts, cancel starting service");
				return null;
			}
		}
		isStop.set(true);
		boolean result = stopService();
		isStop.set(false);
		return result;
	}
	
	/**
	 * 实现启动服务的调用
	 */
	protected abstract boolean stopService() throws Exception;
	
	@Override
	protected void done(){
		try {
			Boolean result = get();
			if(result == null) {
				return;
			}else if(result == true) {
				agentP.stopProxySuccess();
			}else if(result == false){
				agentP.stopProxyFail();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
