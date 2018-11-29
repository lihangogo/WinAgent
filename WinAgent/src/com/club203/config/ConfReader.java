package com.club203.config;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Configuration;
import com.club203.service.Service;
import com.club203.service.openvpn.RemoteConfig;

/**
 * 读取基本的配置，并加载代理信息与当前默认网关
 */
public class ConfReader {
	//加密信息
	private final String encryptInfo;
	//配置信息
	private final Configuration config;
	//运行日志
	private static Logger logger = LoggerFactory.getLogger(ConfReader.class);
	//单例，降低内存开销，为其他模块提供接口
	private static ConfReader confReader = null;
	
	/**
	 * 阻止用户创建实例
	 */
	private ConfReader() {
		this.config=RemoteConfig.getBean();
		this.encryptInfo = "club203";
	}
	
	public static ConfReader getConfig() {
		if(confReader == null) {
			confReader = new ConfReader();
			ProxyReader.getProxy();
			GatewayReader.getDefaultGateway();
			logger.info("Loading basic config successful");
		}
		return confReader;
	}

	public String[] getServiceType() {
		return config.getServiceType();
	}

	public String[] getProxyType() {
		return config.getProxyType();
	}

	public String[] getSchoolServiceType() {
		return config.getSchoolServiceType();
	}

	public String getDefaultPingVerifyServer() {
		return RemoteConfig.getBean_2("defaultPingVerifyServer");
	}
	
	public String getDefaultDnsVerifyServer() {
		return RemoteConfig.getBean_2("defaultDnsVerifyServer");
	}
	
	public String getDefaultHttpVerifyServer() {
		return RemoteConfig.getBean_2("defaultHttpVerifyServer");
	}
	
	public String getEncryptInfo() {
		return encryptInfo;
	}
	
	public HashMap<String, Class<? extends Service>> getStartList() {
		return config.getStartList();
	}
	
	public HashMap<String, Class<? extends Service>> getStopList() {
		return config.getStopList();
	}
	
	public HashMap<String, Class<? extends Service>> getReconnectList() {
		return config.getReconnectList();
	}
}
