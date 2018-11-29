package com.club203.proxy.http;

import com.club203.proxy.ProxyService;

/**
 * @author hehaoxing
 * 建立HTTP PAC局部代理的调用接口
 */
public class HttpPacProxy implements ProxyService {
	//代理服务器IP地址
	private String serverIP;
	//HTTP代理端口号
	private int port;
	//自动代理配置文件位置
	private String pacUrl;
	
	public HttpPacProxy(String serverIP, int port, String pacUrl) {
		super();
		this.serverIP = serverIP;
		this.port = port;
		this.pacUrl = pacUrl;
	}

	@Override
	public int startUp() throws Exception {
		HttpProxy.startpacProxy(serverIP, port, pacUrl);
		return 0;
	}

	@Override
	public int kill() throws Exception {
		HttpProxy.stopProxy();
		return 0;
	}

}
