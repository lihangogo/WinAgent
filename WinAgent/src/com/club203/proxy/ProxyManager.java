package com.club203.proxy;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hehaoxing
 * ProxyManager类，管理ProxyService
 * 代理客户端每次执行一个Service，由一个或多个ProxyService与一系列操作组合完成
 */
public class ProxyManager {
	//当前正在运行的代理列表，以添加的先后顺序排序
	private List<ProxyService> proxyList;
	
	private final static Logger logger = LoggerFactory.getLogger(ProxyManager.class);
	
	private static ProxyManager manager = null;
	
	//禁止外部实例化
	private ProxyManager() { }
	
	public static ProxyManager getManager() {
		if(manager == null) {
			manager = new ProxyManager();
			manager.proxyList = new LinkedList<>();
		}
		return manager;
	}
	
	/**
	 * 添加一个代理服务至链表尾
	 * @param proxy 代理实现类
	 */
	public synchronized ProxyManager addProxy(ProxyService proxy) {
		proxyList.add(proxy);
		return manager;
	}
	
	/**
	 * 移除一个代理服务，每次仅移除一个
	 * @param proxy 代理实现类
	 */
	public synchronized void removeProxy(ProxyService proxy) {
		for(ProxyService tproxy : proxyList) {
			if(tproxy == proxy || tproxy.equals(proxy)) {
				proxyList.remove(tproxy);
			}
		}
	}
	
	/**
	 * 移除所有代理服务
	 * @param proxy 代理实现类
	 */
	public synchronized void removeAllProxy() {
		proxyList.clear();
	}
	
	/**
	 * Manager维护的代理服务数量
	 */
	public int getSize() {
		if(proxyList == null) {
			return -1;
		} else {
			return proxyList.size();
		}
	}
	
	/**
	 * 获取列表中的第一个代理服务
	 */
	public ProxyService getFirstProxy() {
		if(proxyList.size() == 0) {
			return null;
		} else {
			return proxyList.get(0);
		}
	}
	
	/**
	 * 获取第一个proxyClazz类的代理服务
	 */
	public ProxyService getFirstProxy(Class<? extends ProxyService> proxyClass) {
		ProxyService firstProxy = null;
		for(ProxyService proxy : proxyList) {
			if(proxy.getClass().equals(proxyClass)) {
				firstProxy = proxy;
				break;
			}
		}
		return firstProxy;
	}
	
	/**
	 * 获取列表中的最后一个代理服务
	 */
	public ProxyService getLastProxy() {
		if(proxyList.size() == 0) {
			return null;
		} else {
			return proxyList.get(proxyList.size() - 1);
		}
	}
	
	/**
	 * 获取最后一个proxyClazz类的代理服务
	 */
	public ProxyService getLastProxy(Class<? extends ProxyService> proxyClass) {
		ProxyService lastProxy = null;
		for(int i = proxyList.size() - 1; i >=0; i--) {
			ProxyService proxy = proxyList.get(i);
			if(proxy.getClass().equals(proxyClass)) {
				lastProxy = proxy;
				break;
			}
		}
		return lastProxy;
	}

	@Override
	public String toString() {
		String ret = "ProxyManager = [";
		for(ProxyService proxy : proxyList) {
			ret += proxy.toString() + ", ";
		}
		ret += "]";
		return ret;
	}
}
