package com.club203.config;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.club203.beans.Configuration;
import com.club203.service.Service;

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
	//便于重复取用Bean
	private ApplicationContext context = null;
	//单例，降低内存开销，为其他模块提供接口
	private static ConfReader confReader = null;
	
	/**
	 * 阻止用户创建实例
	 */
	private ConfReader() {
		//借助Spring框架读取必要信息
		this.context = new ClassPathXmlApplicationContext("classpath:ApplicationConfig.xml");
		this.config = (Configuration) context.getBean("Config");		
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
		return (String)context.getBean("defaultPingVerifyServer");
	}
	
	public String getDefaultDnsVerifyServer() {
		return (String)context.getBean("defaultDnsVerifyServer");
	}
	
	public String getDefaultHttpVerifyServer() {
		return (String)context.getBean("defaultHttpVerifyServer");
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
