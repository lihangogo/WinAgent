package com.club203.core;

import java.awt.EventQueue;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.club203.beans.Proxy;
import com.club203.config.ProxyReader;
import com.club203.config.ConfReader;
import com.club203.config.GatewayReader;
import com.club203.detect.DetectListener;
import com.club203.dialog.MessageDialog;
import com.club203.proxy.http.HttpProxy;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;
import com.club203.utils.NetworkUtils;

/**
 * 	业务执行：
 * 		MVP设计模式
 * 		此类除了启动代理，还为service中类和DetectListener提供了方法调用
 */
public class AgentPresenter {
	
	//记录使用本软件的开始时间和结束时间，用于计费
	private long startTime=0;
	private long stopTime=0;
	
	//维护一个AgentModel
	private AgentModel agentModel = null;
	//维护一个GUI的引用，用于更新界面
	private AgentView agentView;
	
	//代理的ProxyType与具体服务实现的类的对应
	private final HashMap<String, Class<? extends Service>> startList;
	private final HashMap<String, Class<? extends Service>> stopList;
	private final HashMap<String, Class<? extends Service>> reconnectList;
	//控制器维护了当前的工作状态信息(即一个完成的代码程序流程)
	private volatile AtomicBoolean isWork = new AtomicBoolean(false);
	//日志输出
	private final static Logger logger = LoggerFactory.getLogger(AgentPresenter.class);
	
	private static AgentPresenter agentPresenter = null;
	
	private AgentPresenter() {
		logger.info("Loading config from config file");
		Openvpn.killall();
		//读取配置文件与网关
		ConfReader config = ConfReader.getConfig();
		AgentView.changeSwingSkin();
		//一些变量的初始化
		agentModel = AgentModel.getInstance();
		startList = config.getStartList();
		stopList = config.getStopList();
		reconnectList = config.getReconnectList();
	}
	
	public static AgentPresenter getAgentPresenter() {
		if(agentPresenter == null) {
			agentPresenter = new AgentPresenter();
			agentPresenter.init();
		}
		return agentPresenter;
	}
	
	/**
	 * 代理初始化
	 */
	public void init() {
		logger.info("Starting initializing winagent");
		EventQueue.invokeLater(()->agentView = new AgentView("WinAgent"));
		//EventQueue.
		Openvpn.init();
		NetworkUtils.cleanDNSCache();
		NetworkUtils.disableJVMDNSCache();
		logger.info("Initializing winagent success");
	}
	
	/**
	 * 触发代理关闭
	 */
	public void shutdown() {
		try{
			agentView.setVisible(false);
			logger.info("Stoping winagent");
			DetectListener.getInstance().cancel();		
			//有打开的代理则关闭
			if(agentModel.getCurrentProxy() != null)
				stopProxy();
			//清理OpenVPN客户端
			Openvpn.clean();
			//清理HTTP代理
			HttpProxy.stopProxy();
			//重置路由表与DNS
			resetRouteTable();
			NetworkUtils.resetDNS();
		}catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 用户点击打开代理
	 * @param serviceType 服务类型，对应UI的选框
	 * @param proxyName   代理名
	 */
	public boolean openClicked(int serviceType, String proxyName) {
		if(proxyName == null){
			new MessageDialog("没有代理被选中.").show();
			logger.info("No proxy selected");
			return false;
		}
		if(isWork.get() == true){
			logger.info("Click repeatedly");
			return false;
		}
		if (agentModel.isConnected() == false){
			isWork.set(true);
			agentView.setGuiText("正在建立连接");
			Map<String, Proxy> proxyList = ProxyReader.getProxy();
			Proxy proxy = proxyList.get(proxyName);
			return startProxy(proxy);
		}
		return false;
	}
	
	/**
	 * 用户点击关闭代理
	 * @param serviceType 服务类型，对应UI的选框
	 */
	public boolean closeClicked(int serviceType) {
		if(isWork.get() == true){
			logger.info("Click repeatedly");
			return false;
		}
		if (agentModel.getProxyStatus(serviceType) == true){
			isWork.set(true);
			agentView.setGuiText("正在关闭连接");
			return stopProxy();
		}
		return false;
	}
	
	/**
	 * 启动代理
	 * @param proxy 代理数据信息
	 */
	public boolean startProxy(Proxy proxy) {
		//加载当前客户端并建立连接
		if(proxy == null) {
			return false;
		}
		String proxyName = proxy.getProxyName();
		String serviceType = proxy.getServiceType();
		String proxyType = proxy.getProxyType();
		logger.info(proxyName + ": Starting connecting to server");
		agentView.setGuiText(proxyName + ": 正在建立连接");
		
		String[] serviceTypeList = ConfReader.getConfig().getServiceType();
		for(int i = 0; i < serviceTypeList.length; i++) {
	    	if(serviceType.equals(serviceTypeList[i])) {
	    		try{
	    			Class<? extends Service> clazz = startList.get(proxyType);
	    			if(clazz != null) {
	    				Field field = clazz.getField("factory");
			    		ServiceFactory serviceFactory = (ServiceFactory)field.get(clazz);
			    		serviceFactory.getService(proxy).execute();
			    		//设置当前代理信息，这里在打开代理时进行设置，目的是能够及时取消
			    		agentModel.setCurrentProxy(proxy);
			    		agentModel.setProxyStatus(proxy.getServiceTypeIndex(), true);
			    		buttonStatusModify();
			    		isWork.set(false);
			    		return true;
	    			}
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    			//找不到建连的实现或者对应的字段，取消连点检测
	    			isWork.set(false);
	    			agentView.setGuiText(proxyName + ": 建立连接失败");
	    			logger.info(proxyName + ": Connecting to server failed");
	    			return false;
				}
	    	}
	    }
		isWork.set(false);
		agentView.setGuiText(proxyName + ": 建立连接失败");
		logger.info(proxyName + ": Connecting to server failed");
		return false;
	}
	
	/**
	 * 成功打开代理后执行操作
	 * @param proxy 代理数据信息
	 */
	public void startProxySuccess(Proxy proxy) {
		new MessageDialog("代理配置成功.").show();
		agentView.setGuiText(proxy.getProxyName() + ": 代理连接成功");
		logger.info("Proxy established successful");
		agentModel.printAgent();
	}
	
	/**
	 * 打开代理失败后执行操作
	 * @param proxy 代理数据信息
	 */
	public void startProxyFail(Proxy proxy) {
		//将当前代理信息还原
		agentModel.setCurrentProxy(null);
		agentModel.setProxyStatus(proxy.getServiceTypeIndex(), false);
		buttonStatusModify();
		new MessageDialog("代理配置失败.").show();
		agentView.setGuiText(proxy.getProxyName() + ": 代理连接失败");
		logger.info("Fail to establish proxy connection");
		agentModel.printAgent();
	}
	
	/**
	 * 打开代理的过程中点击关闭的操作
	 * @param proxy 代理数据信息
	 */
	public void stopProxyWhenStart(Proxy proxy) {
		logger.info("Click stop when start");
		stopProxy();
	}
	
	/**
	 * 关闭当前代理
	 */
	public boolean stopProxy() {
		Proxy currentProxy = agentModel.getCurrentProxy();
		if(currentProxy == null) {
			return true;
		} else {
			String proxyName = currentProxy.getProxyName();
			String serviceType = currentProxy.getServiceType();
			String proxyType = currentProxy.getProxyType();
			logger.info(proxyName + ": Starting closing a connection");
			agentView.setGuiText(proxyName + ": 正在关闭连接");
			
			String[] serviceTypeList = ConfReader.getConfig().getServiceType();
			for(int i = 0; i < serviceTypeList.length; i++) {
		    	if(serviceType.equals(serviceTypeList[i])){
		    		try{
		    			Class<? extends Service> clazz = stopList.get(proxyType);
		    			if(clazz != null) {
		    				Field field = clazz.getField("factory");
				    		ServiceFactory serviceFactory = (ServiceFactory)field.get(clazz);
				    		serviceFactory.getService(currentProxy).execute();
				    		return true;
		    			}
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    			isWork.set(false);
		    			agentView.setGuiText(proxyName + ": 连接关闭失败");
		    			logger.info(proxyName + ": Closing connection failed");
		    			return false;
					}
		    	}
		    }
		}
		isWork.set(false);
		agentView.setGuiText("连接关闭失败");
		return false;
	}
	
	/**
	 * 代理被成功关闭后执行的操作
	 */
	public void stopProxySuccess() {
		Proxy currentproxy = agentModel.getCurrentProxy();
		agentModel.setProxyStatus(currentproxy.getServiceTypeIndex(), false);
		agentModel.setCurrentProxy(null);
		buttonStatusModify();
		new MessageDialog("代理关闭成功.").show();
		agentView.setGuiText(currentproxy.getProxyName() + ": 代理关闭成功");
		logger.info("Proxy is stopped sucessful");
		isWork.set(false);
		agentModel.printAgent();
	}
	
	/**
	 * 代理关闭失败后执行的操作
	 */
	public void stopProxyFail() {
		Proxy currentproxy = agentModel.getCurrentProxy();
		new MessageDialog("代理关闭失败.").show();
		agentView.setGuiText(currentproxy.getProxyName() + ": 代理关闭失败");
		logger.info("Proxy is stopped failed");
		isWork.set(false);
		agentModel.printAgent();
	}
	
	/**
	 * 断线重连(静默执行，不弹窗)
	 */
	public boolean reconnectProxy() {
		Proxy currentProxy = agentModel.getCurrentProxy();
		if(currentProxy == null) {
			return true;
		} else {
			String proxyName = currentProxy.getProxyName();
			String serviceType = currentProxy.getServiceType();
			String proxyType = currentProxy.getProxyType();
			logger.info(proxyName + ": Triggering reconnection");
			
			String[] serviceTypeList = ConfReader.getConfig().getServiceType();
			for(int i = 0; i < serviceTypeList.length; i++) {
		    	if(serviceType.equals(serviceTypeList[i])){
		    		try{
		    			Class<? extends Service> clazz = reconnectList.get(proxyType);
		    			if(clazz != null) {
		    				Field field = clazz.getField("factory");
				    		ServiceFactory serviceFactory = (ServiceFactory)field.get(clazz);
				    		serviceFactory.getService(currentProxy).execute();
				    		return true;
		    			}
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    			logger.info(proxyName + ": Reconnecting failed");
		    			return false;
					}
		    	}
		    }
		}
		return false;
	}
	
	/**
	 * 重新连接代理成功时执行的操作
	 * @param proxy 代理数据信息
	 */
	public void reconnectSuccess(Proxy proxy) {
		//new MessageDialog("代理连接恢复成功.").show();
		logger.info(proxy.getProxyName() + "： Reconnect to proxy sucessful");
		logger.info(agentModel.printAgent());
	}
	
	/**
	 * 重新连接代理失败时执行的操作
	 * @param proxy 代理数据信息
	 */
	public void reconnectFail(Proxy proxy) {
		//new MessageDialog("代理连接恢复失败.").show();
		logger.info(proxy.getProxyName() + "： Reconnect to proxy failed");
		//断路恢复失败，则关闭连接
		stopProxy();
		logger.info(agentModel.printAgent());
	}
	
	/**
	 * 根据Model状态更新Agent的按键
	 */
	public void buttonStatusModify() {
		boolean isConnect = false;
		String[] serviceTypeList = ConfReader.getConfig().getServiceType();
		for(int i = 0; i < serviceTypeList.length; i++) {
			agentView.setOpenButton(i, false);
			if(agentModel.getProxyStatus(i + 1) == true) {
				agentView.setCloseButton(i, true);
			}else {
				agentView.setCloseButton(i, false);
			}
			isConnect = agentModel.getProxyStatus(i + 1) | isConnect;
		}
		if(isConnect == false){
			for(int i = 0; i < serviceTypeList.length; i++) {
				agentView.setOpenButton(i, true);
			}
		}
	}
	
	/**
	 * 保留单一默认出口，使得默认路由为初始默认路由
	 */
	public boolean resetRouteTable() {
		//添加默认路由，删除多网卡的推送路由
		List<String> commandList = new ArrayList<>();
		//恢复当前路由表
		String defaultGateWay = GatewayReader.getDefaultGateway();
		List<String> anotherGateWay = GatewayReader.getAnotherGateway();
		commandList.add("route add 0.0.0.0 mask 0.0.0.0 " + defaultGateWay);
		for(String gw : anotherGateWay) {
			commandList.add("route delete -p 0.0.0.0 mask 0.0.0.0 " + gw);
		}
		try {
			for(String command : commandList) {
				logger.info("Execute command: " + command);
				Runtime.getRuntime().exec(command).waitFor();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
