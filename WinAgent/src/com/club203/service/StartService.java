package com.club203.service;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.core.AgentPresenter;

/**
 * @author hehaoxing
 * 用于开启代理连接
 */
public abstract class StartService extends Service {

	//通知操作执行完成
	private AgentPresenter agentP = AgentPresenter.getAgentPresenter();
		
	private final static Logger logger = LoggerFactory.getLogger(StartService.class);
	
	public StartService(Proxy proxy) {
		super(proxy);
	}
	
	@Override
	protected Boolean doInBackground() throws Exception {
		isStart.set(true);
		boolean result = startService();
		//只能在打开后才能完成关闭撤销
		synchronized (isStop) {
			//判断是否需要被取消
			if(isStop.get() == true) {
				//这里返回null，触发停止线程撤销连接
				isStart.set(false);
				logger.info("Starting service fails, stopping service starts");
				return null;
			}
		}
		isStart.set(false);
		return result;
	}
	
	/**
	 * 实现启动服务的调用
	 */
	protected abstract boolean startService() throws Exception;

	/**
	 * 代理打开成功或失败后的动作
	 */
	@Override
	protected void done(){
		try {
			Boolean result = get();
			if(result == null) {
				agentP.stopProxyWhenStart(proxy);
			}else if(result == true) {
				agentP.startProxySuccess(proxy);
			}else {
				agentP.startProxyFail(proxy);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
