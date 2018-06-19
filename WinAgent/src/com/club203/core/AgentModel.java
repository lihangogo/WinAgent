package com.club203.core;

import com.club203.beans.Proxy;
import com.club203.config.ConfReader;

/**
 * @author hehaoxing
 * 
 * 维护当前代理的工作状态
 * 仅与AgentPresenter通信改变状态
 */
public class AgentModel {
	//代理开关状态(由GUI使用)
	private Boolean[] proxyStatus;
	//当前代理类(当前状态)
	private Proxy currentProxy;
	//维护AgentModel的一个单例
	private static AgentModel agentModel = null;
	
	public static AgentModel getInstance() {
		if(agentModel == null) {
			agentModel = new AgentModel();
		}
		return agentModel;
	}
	
	private AgentModel() {
		//代理在刚开始运行时，所有的代理都是被关闭的
		String[] serviceTypeList = ConfReader.getConfig().getServiceType();
		proxyStatus = new Boolean[serviceTypeList.length];
		for(int i = 0; i < serviceTypeList.length; i++){
			proxyStatus[i] = false;
		}
		//开始时没有启动的代理
		currentProxy = null;
	}
	
	/**
	 * 获取不同服务类型的当前状态
	 * @return: Boolean：True,False,Null
	 */
	public Boolean getProxyStatus(int serviceType) {
		//服务种类配置从1开始，索引从0开始
		serviceType = serviceType - 1;
		if(serviceType >= 0 && serviceType < proxyStatus.length){
			return proxyStatus[serviceType];
		}else {
			return null;
		}
	}

	/**
	 * 设置开关状态
	 */
	public void setProxyStatus(int serviceType, boolean operation) {
		//服务种类配置从1开始，索引从0开始
		serviceType = serviceType - 1;
		if(operation == true){
			proxyStatus[serviceType] = true;
		}else {
			proxyStatus[serviceType] = false;
		}
	}
	
	/**
	 * 控制器能够获取当前代理信息
	 */
	public Proxy getCurrentProxy() {
		return currentProxy;
	}

	public void setCurrentProxy(Proxy currentProxy) {
		this.currentProxy = currentProxy;
	}

	/**
	 * 通过检查状态位，返回是否有当前建立的连接
	 */
	public boolean isConnected() {
		boolean isConnect = false;
		for(int i = 0; i < proxyStatus.length; i++) {
			isConnect = isConnect | proxyStatus[i];
		}
		return isConnect;
	}

	/**
	 * 输出当前的任务信息
	 */
	public String printAgent() {
		String str = " ";
		str += "Current Agent Status :\n";
		str += "CurrentProxy : " + currentProxy + "\n";
		str += "ProxyStatus : \n";
		
		for(int i = 0; i < proxyStatus.length; i++) {
			str += "\tService " + (i + 1) + " : " +proxyStatus[i] + "\n";
		}
		return str;
	}
	
}
