package com.club203.service.route;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.config.GatewayReader;
import com.club203.detect.DetectListener;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;
import com.club203.service.StopService;
import com.club203.utils.NetworkUtils;

public class StopRouteProxy extends StopService {
	
	private final static Logger logger = LoggerFactory.getLogger(StopRouteProxy.class);
	
	public StopRouteProxy(Proxy proxy) {
		super(proxy);
	}

	@Override
	protected boolean stopService() throws Exception {
		List<String> ServerIPList = proxy.getServerIP();
		String gateway = GatewayReader.getDefaultGateway();
		List<String> anotherGateWay = GatewayReader.getAnotherGateway();
		
		//装填路由表
		List<String> commandList = new ArrayList<>();
		for(String ServerIP : ServerIPList) {
			commandList.add("route -p delete 0.0.0.0 mask 0.0.0.0 " + ServerIP);
		}
		//清理非正常网关，保证退出登录后可以正常使用校园网
		for(String gw : anotherGateWay) {
			commandList.add("route delete 0.0.0.0 mask 0.0.0.0 " + gw);
		}
		commandList.add("route add 0.0.0.0 mask 0.0.0.0 " + gateway);
		
		try{
			logger.info("Starting stoping route proxy");
			for(String command : commandList) {
				Runtime.getRuntime().exec(command).waitFor();
				logger.info("Execute command: " + command);
			}
			//关闭心跳检测
			DetectListener.getInstance().cancel();
		} catch(Exception e){
			logger.info("Stopping route proxy failed");
		    return false;
		}
		//启动线程，取消原先设置的DNS地址
		new Thread(() -> NetworkUtils.resetDNS()).start();
		return true;
	}
	
	public static ServiceFactory factory = new ServiceFactory() {	
		@Override
		public Service getService(Proxy proxy) {
			return new StopRouteProxy(proxy);
		}
	};
}
