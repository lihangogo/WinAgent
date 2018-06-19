package com.club203.service;

import java.util.List;

import com.club203.beans.Proxy;
import com.club203.config.ConfReader;

/**
 * @author hehaoxing
 * 服务工具类
 */
public class ServiceUtils {
	
	/**
	 * 获取用于建连检测的IP地址
	 * 取第一项或配置文件项
	 * @param proxy 代理信息类
	 */
	public static String getProxyVerifyIP(Proxy proxy) {
		List<String> serverList = proxy.getProxyVerifyServer();
		if(serverList != null && serverList.size() >= 1) {
			return serverList.get(0);
		} else {
			return ConfReader.getConfig().getDefaultPingVerifyServer();
		}
	}
	
	/**
	 * 获取用于建连检测的DNS域名
	 */
	public static String getDnsVerifyHost(Proxy proxy) {
		List<String> serverList = proxy.getDnsVerifyServer();
		if(serverList != null && serverList.size() >= 1) {
			return serverList.get(0);
		} else {
			return ConfReader.getConfig().getDefaultDnsVerifyServer();
		}
	}
	
	/**
	 * 获取用于Http代理建连检测域名
	 */
	public static String getHttpProxyVerifyHost(Proxy proxy) {
		List<String> serverList = proxy.getProxyVerifyServer();
		if(serverList != null && serverList.size() >= 1) {
			return serverList.get(0);
		} else {
			return ConfReader.getConfig().getDefaultHttpVerifyServer();
		}
	}
}
