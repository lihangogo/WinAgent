package com.club203.proxy.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.proxy.ProxyService;

/**
 * @author hehaoxing
 * 建立全局HTTP代理的调用接口
 */
public class HttpGlobalProxy implements ProxyService{
	//代理服务器IP地址
	private String serverIP;
	//HTTP代理端口号
	private int port;
	//日志输出
	private final static Logger logger = LoggerFactory.getLogger(HttpGlobalProxy.class);
	
	public HttpGlobalProxy(String serverIP, int port) {
		super();
		this.serverIP = serverIP;
		this.port = port;
	}
	
	@Override
	public int startUp() throws Exception {
		HttpProxy.startglobalProxy(serverIP, port);
		return 0;
	}
 
	@Override
	public int kill() throws Exception {
		HttpProxy.stopProxy();
		return 0;
	}

}
