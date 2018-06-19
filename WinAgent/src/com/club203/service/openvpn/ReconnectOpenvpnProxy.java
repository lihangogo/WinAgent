package com.club203.service.openvpn;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.config.GatewayReader;
import com.club203.detect.DetectListener;
import com.club203.dialog.MessageDialog;
import com.club203.exception.openvpn.OpenVPNException;
import com.club203.proxy.ProxyManager;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.service.ReconnectService;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;
import com.club203.service.ServiceUtils;
import com.club203.utils.NetworkUtils;

/**
 * @author hehaoxing
 * 
 * 执行OpenVPN代理的断线重连操作:
 * 	   	若仅补全路由表不能正常访问互联网，则撤销隧道并重新建连
 *   	不改变ProxyManager所维护的代理服务
 */
public class ReconnectOpenvpnProxy extends ReconnectService{

	private final static Logger logger = LoggerFactory.getLogger(ReconnectOpenvpnProxy.class);
	
	public ReconnectOpenvpnProxy(Proxy proxy) {
		super(proxy);
	}
	 
	@Override
	protected boolean reconnectService() throws Exception {
		ProxyManager manager = ProxyManager.getManager();
		Openvpn openvpn = (Openvpn)ProxyManager.getManager().getFirstProxy(Openvpn.class);
		if(!(manager.getSize() == 1 && openvpn != null)) {
			return false;
		}
		//必要的路由表
		List<String> commandList = new LinkedList<>();
		commandList.add("route -p delete 0.0.0.0 mask 0.0.0.0 " + proxy.getVirtualIP().get(0));
		if(proxy.isSchoolService() == true) {
			commandList.add("route add 10.0.0.0 mask 255.0.0.0 " + GatewayReader.getDefaultGateway());
		} else {
			for(String ServerIP : proxy.getServerIP()) {
				commandList.add("route add " + OpenvpnUtils.ipEncrypt(ServerIP) + " mask 255.255.255.0 " 
						+ GatewayReader.getDefaultGateway());
			}
		}
		//保证目标服务器可以被路由到
		for(String command : commandList) {
			Runtime.getRuntime().exec(command).waitFor();
			logger.info("Execute command: " + command);
		}
		
		//隧道连通但触发了重连，隧道出口服务器访问网络失败
		if(NetworkUtils.ping2(proxy.getVirtualIP().get(0), 3, 500)) {
			return false;
		} else {
			//隧道不连通，清路由，然后重新下隧道
			openvpn.kill();
			boolean isConnect = false;
			for(String ServerIP : proxy.getServerIP()) {
				isConnect = isConnect | NetworkUtils.ping2(ServerIP, 3, 500);
				if(isConnect == true) {
					break;
				}
			}
			if(isConnect == false) {
				logger.info("Reconnected connect to OpenVPN server failed, no servers can be connected");
				return false;
			}
			try {
				int result = openvpn.startUp();
				//添加默认路由，防止校园网关覆盖
				commandList.clear();
				commandList.add("route -p delete 0.0.0.0 mask 0.0.0.0 " + proxy.getVirtualIP().get(0));
				commandList.add("route -p add 0.0.0.0 mask 0.0.0.0 " + proxy.getVirtualIP().get(0));
				for(String command : commandList) {
					Runtime.getRuntime().exec(command).waitFor();
					logger.info("Execute command: " + command);
				}

				String proxyVerifyServer = ServiceUtils.getProxyVerifyIP(proxy);
				String dnsVerifyServer = ServiceUtils.getDnsVerifyHost(proxy);
				if(result == 0 && waitForConnected(proxyVerifyServer, dnsVerifyServer) == true){
					NetworkUtils.setDNS(proxy.getDnsServer());
					logger.info("Connecting to OpenVPN sucessful");
					DetectListener.getInstance().cancel();
					OpenvpnUtils.getDetectListener(proxy).start();
					return true;
				} else {
					logger.info("Reconnect to OpenVPN server failed");
				}
			} catch(OpenVPNException e){
				logger.info("Error : " + e.toString());
				new MessageDialog(e.toString()).show();
			};
			openvpn.kill();
		}
		//触发StopOpenvpnProxy
		return false;
	}
	
	/**
	 * 等待连接建立成功
	 * 建立成功返回true，否则返回false
	 */
	private boolean waitForConnected(String ipaddress, String dnsCheckAddress){
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
			return new ReconnectOpenvpnProxy(proxy);
		}
	};
}
