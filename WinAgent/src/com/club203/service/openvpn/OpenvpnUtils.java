package com.club203.service.openvpn;

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

/**
 * @author hehaoxing
 * 在OpenVPN建连中多个流程使用的方法(一般是连接与重连)
 */
public class OpenvpnUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(OpenvpnUtils.class);
	
	/**
	 * 取IP地址所在的24为子网掩码网段
	 * @param ipaddress 服务器IP地址
	 */
	protected static String ipEncrypt(String ipaddress) {
		String[] ipframe = ipaddress.split("\\.");
		return ipframe[0] + "." + ipframe[1] + "." + ipframe[2] + "." + "0";
	}
	
	/**
	 * 初始化代理可用监听器
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
	 * 代理配置回滚，保证代理建立失败后能够回到正常状态
	 * 用于连接建立与连接重连
	 */
	protected static boolean routeRollback(Proxy proxy) {
		//基本信息
		String Gateway = GatewayReader.getDefaultGateway();
		String VirtualIP = proxy.getVirtualIP().get(0);
		List<String> ServerIPList = proxy.getServerIP();

		List<String> commandList = new ArrayList<>();
		commandList.add("route -p delete 0.0.0.0 mask 0.0.0.0 " + VirtualIP);
		commandList.add("route add 0.0.0.0 mask 0.0.0.0 " + Gateway);
		//校园网内与校园网外不同的连接方式
		if(proxy.isSchoolService() == true) {
			commandList.add("route delete 10.0.0.0 mask 255.0.0.0 " + Gateway);
		} else {
			for(String ServerIP : ServerIPList) {
				commandList.add("route delete " + ServerIP +" mask 255.255.255.255 " + Gateway);
			}
		}
		try {
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
