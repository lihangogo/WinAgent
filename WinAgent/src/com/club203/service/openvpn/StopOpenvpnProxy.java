package com.club203.service.openvpn;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.config.GatewayReader;
import com.club203.config.ConfReader;
import com.club203.detect.DetectListener;
import com.club203.proxy.ProxyManager;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;
import com.club203.service.StopService;
import com.club203.utils.NetworkUtils;

public class StopOpenvpnProxy extends StopService {
	
	private final static Logger logger = LoggerFactory.getLogger(StopOpenvpnProxy.class);
	
	public StopOpenvpnProxy(Proxy proxy) {
		super(proxy);
	}

	@Override
	protected boolean stopService() throws Exception {
		ProxyManager manager = ProxyManager.getManager();
		Openvpn openvpn = (Openvpn)ProxyManager.getManager().getFirstProxy(Openvpn.class);
		if(!(manager.getSize() == 1 && openvpn != null)) {
			return false;
		}
		
		List<String> ServerIPList = proxy.getServerIP();
		String gateway = GatewayReader.getDefaultGateway();
		String VirtualIP = proxy.getVirtualIP().get(0);
		List<String> anotherGateWay = GatewayReader.getAnotherGateway();
		
		//装填路由表命令
		List<String> commandList = new ArrayList<>();
		commandList.add("route -p delete 0.0.0.0 mask 0.0.0.0 " + VirtualIP);
		//清理非正常网关，保证退出登录后可以正常使用校园网
		for(String gw : anotherGateWay) {
			commandList.add("route delete 0.0.0.0 mask 0.0.0.0 " + gw);
		}
		commandList.add("route add 0.0.0.0 mask 0.0.0.0 " + gateway);
		//校园网内与校园网外不同的连接方式
		if(isSchoolService() == true) {
			commandList.add("route delete 10.0.0.0 mask 255.0.0.0 " + gateway);
		} else {
			for(String ServerIP : ServerIPList) {
				commandList.add("route delete " + ipEncrypt(ServerIP) + " mask 255.255.255.0 " + gateway);
			}
		}
		
		try{
			logger.info("Starting stoping openVPN client");
			for(String command : commandList) {
				Runtime.getRuntime().exec(command).waitFor();
				logger.info("Execute command: " + command);
			}
			DetectListener.getInstance().cancel();	
			openvpn.kill();
			manager.removeProxy(openvpn);
			new Thread(() -> NetworkUtils.resetDNS()).start();
			return true;
		} catch(Exception e){ };
		return false;
	}
	
	public boolean isSchoolService() {
		String ServiceType = proxy.getServiceType();
		String[] SchoolServiceType = ConfReader.getConfig().getSchoolServiceType();
		for(int i = 0; i < SchoolServiceType.length; i++) {
			if(ServiceType.equals(SchoolServiceType[i])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 取子网掩码255.255.255.0对应的子网网段
	 */
	public String ipEncrypt(String ipaddress) {
		String[] ipframe = ipaddress.split("\\.");
		return ipframe[0] + "." + ipframe[1] + "." + ipframe[2] + "." + "0";
	}
	
	public static ServiceFactory factory = new ServiceFactory() {	
		@Override
		public Service getService(Proxy proxy) {
			return new StopOpenvpnProxy(proxy);
		}
	};
}
