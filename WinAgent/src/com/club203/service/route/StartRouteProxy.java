package com.club203.service.route;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.config.GatewayReader;
import com.club203.dialog.MessageDialog;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;
import com.club203.service.ServiceUtils;
import com.club203.service.StartService;
import com.club203.utils.NetworkUtils;

/**
 * 指定在同一个二层网络中，通过配置默认路由接入
 */
public class StartRouteProxy extends StartService {

	private final static Logger logger = LoggerFactory.getLogger(StartRouteProxy.class);
	
	public StartRouteProxy(Proxy proxy) {
		super(proxy);
	}

	@Override
	protected boolean startService() throws Exception {
		List<String> ServerIPList = proxy.getServerIP();
		String Gateway = GatewayReader.getDefaultGateway();
		List<String> commandList = new ArrayList<>();
		//同一网关修改路由接入，选择能够接入的第一项即可
		for(String ServerIP : ServerIPList) {
			if(NetworkUtils.ping2(ServerIP, 3, 500) == false){
				logger.info("Server " + ServerIP + " cannot be connected");
			} else {
				//能Ping不能连的原因：没有设置路由转发、没有配置NAT(需要的情况下)、没有接外网
				commandList.clear();
				commandList.add("route -p delete 0.0.0.0 mask 0.0.0.0 " + Gateway);
				commandList.add("route -p add 0.0.0.0 mask 0.0.0.0 " + ServerIP);
				try {
					for(String command : commandList) {
						Runtime.getRuntime().exec(command).waitFor();
						logger.info("Execute command: " + command);
					}
					NetworkUtils.setDNS(proxy.getDnsServer());
					Thread.sleep(300);			
					String proxyVerifyServer = ServiceUtils.getProxyVerifyIP(proxy);
					String dnsVerifyServer = ServiceUtils.getDnsVerifyHost(proxy);
					boolean usable = waitForConnected(proxyVerifyServer, dnsVerifyServer);
					if(usable == false){
						logger.info("Sever:" + ServerIP + " cannot be used, rollback");
						commandList.clear();
						while (RouteUtils.routeRollback(ServerIP) == true);
						new Thread(() -> NetworkUtils.resetDNS()).start();
					} else {
						RouteUtils.getDetectListener(proxy).start();
						return true;
					}
				} catch (Exception e) { }
			}
		}
		new MessageDialog("无法接入服务器").show();
		logger.info("Cannot connect to proxy server, no servers can be used");
		return false;
	}
	
	public boolean waitForConnected(String ipaddress, String dnsCheckAddress){
		for(int i = 0; i < 3; i++){
			if(NetworkUtils.ping2(ipaddress, 3, 500) == true){
				for(int j = 0; j <= 1; j++) {
					if(NetworkUtils.dig(dnsCheckAddress) == true) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static ServiceFactory factory = new ServiceFactory() {	
		@Override
		public Service getService(Proxy proxy) {
			return new StartRouteProxy(proxy);
		}
	};
}
