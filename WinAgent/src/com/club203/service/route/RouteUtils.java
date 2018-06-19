package com.club203.service.route;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.config.ConfReader;
import com.club203.config.GatewayReader;
import com.club203.detect.DetectListener;
import com.club203.detect.internal.DnsListener;
import com.club203.detect.internal.InetPingListener;
import com.club203.detect.internal.NetworkHold;

public class RouteUtils {

	private final static Logger logger = LoggerFactory.getLogger(RouteUtils.class);
	
	/**
	 * 初始化代理可用监听器
	 * @param proxy 代理信息类
	 */
	protected static DetectListener getDetectListener(Proxy proxy) {
		DetectListener listener = DetectListener.getInstance().init();
		//用于检测Ping
		List<String> pingVerifyList = proxy.getProxyVerifyServer();
		//用于检测DNS
		List<String> dnsVerifyList = proxy.getDnsVerifyServer();
		//添加自定义检测域名：同时填写Ping
		if(pingVerifyList != null && pingVerifyList.size() >= 1) {
			for(String server : pingVerifyList) {
				//listener.adddetectServices(new PingListener(server, 500));
				listener.adddetectServices(new InetPingListener(server, 2000, 5000));
			}
		} else {
			//listener.adddetectServices(new PingListener(ConfReader.getConfig().getDefaultPingVerifyServer(), 500));
			listener.adddetectServices(new InetPingListener(ConfReader.getConfig().getDefaultPingVerifyServer(), 2000, 5000));
		}
		//添加自定义DNS检测域名
		if(dnsVerifyList != null && dnsVerifyList.size() >= 1) {
			for(String server : dnsVerifyList) {
				listener.adddetectServices(new DnsListener(server));
			}	
		} else {
			listener.adddetectServices(new DnsListener(ConfReader.getConfig().getDefaultDnsVerifyServer()));
		}
		listener.adddetectServices(new NetworkHold());
		return listener;
	}
	
	/**
	 * 代理失败后路由表回滚
	 */
	protected static boolean routeRollback(String ServerIP) {
		String Gateway = GatewayReader.getDefaultGateway();
		List<String> commandList = new ArrayList<>();
		commandList.add("route -p delete 0.0.0.0 mask 0.0.0.0 " + ServerIP);
		commandList.add("route add 0.0.0.0 mask 0.0.0.0 " + Gateway);
		try{
			for(String command : commandList) {
				Runtime.getRuntime().exec(command).waitFor();
				logger.info("Execute command: " + command);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
