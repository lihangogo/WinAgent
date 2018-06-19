package com.club203.service.route;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.detect.DetectListener;
import com.club203.service.ReconnectService;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;
import com.club203.service.ServiceUtils;
import com.club203.utils.NetworkUtils;

/**
 * @author hehaoxing
 * 实现路由代理的断路重连
 */
public class ReconnectRouteProxy extends ReconnectService{

	private final static Logger logger = LoggerFactory.getLogger(ReconnectRouteProxy.class);
	
	public ReconnectRouteProxy(Proxy proxy) {
		super(proxy);
	}

	/**   
	 * 实现路由代理的断路重连，这样只关心具体业务实现即可
	 * 	       与开启代理的区别是，没有必要为各个网卡设置的DNS地址，降低时间开销
	 */  
	@Override
	protected boolean reconnectService() throws Exception {
		logger.info("Try to reconnect to proxv: " + proxy.getProxyName());
		DetectListener.getInstance().cancel();
		List<String> ServerIPList = proxy.getServerIP();
		for(String ServerIP : ServerIPList) {
			if(NetworkUtils.ping2(ServerIP, 3, 500) == false){
				logger.info("Server " + ServerIP + "cannot be connected");
			} else {
				//出现问题后，尝试建立与其他主机的连接
				List<String> commandList = new ArrayList<>();
				commandList.add("route -p delete 0.0.0.0 mask 0.0.0.0");
				commandList.add("route -p add 0.0.0.0 mask 0.0.0.0 " + ServerIP);
				try {
					for(String command : commandList) {
						Runtime.getRuntime().exec(command).waitFor();
						logger.info("Execute command: " + command);
					}
					NetworkUtils.setDNS(proxy.getDnsServer());
					String proxyVerifyServer = ServiceUtils.getProxyVerifyIP(proxy);
					String dnsVerifyServer = ServiceUtils.getDnsVerifyHost(proxy);
					boolean usable = waitForConnected(proxyVerifyServer, dnsVerifyServer);
					//连接不可用的情况，撤销连接
					if(usable == false){
						logger.info("Sever:" + ServerIP + " cannot be used, rollback");
						commandList.clear();
						while (RouteUtils.routeRollback(ServerIP) == true);
					} else {
						//重新启动网络状态监听
						RouteUtils.getDetectListener(proxy).start();
						return true;
					}
				} catch (Exception e) { }
			}
		}
		return false;
	}
	
	/**
	 * 等待连接建立成功
	 * 建立成功返回true，否则返回false
	 */
	public boolean waitForConnected(String ipaddress, String dnsAddress){
		for(int i = 0; i < 3; i++){
			if(NetworkUtils.ping2(ipaddress, 3, 500) == true){
				for(int j = 0; j <= 1; j++) {
					if(NetworkUtils.dig(dnsAddress) == true) {
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
			return new ReconnectRouteProxy(proxy);
		}
	};
}
