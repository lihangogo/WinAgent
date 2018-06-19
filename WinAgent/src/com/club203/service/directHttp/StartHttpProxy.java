package com.club203.service.directHttp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.config.ConfReader;
import com.club203.detect.DetectListener;
import com.club203.detect.internal.HttpProxyListener;
import com.club203.detect.internal.InetPingListener;
import com.club203.dialog.MessageDialog;
import com.club203.dialog.OpenVPNAuthDlg;
import com.club203.exception.openvpn.OpenVPNException;
import com.club203.proxy.ProxyManager;
import com.club203.proxy.ProxyService;
import com.club203.proxy.http.HttpGlobalProxy;
import com.club203.proxy.http.HttpPacProxy;
import com.club203.proxy.http.HttpProxy;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;
import com.club203.service.ServiceUtils;
import com.club203.service.StartService;
import com.club203.utils.NetworkUtils;

/**
 * @author hehaoxing
 * 启动HTTP代理，使用接入点作为代理服务器
 * 连接方法：先通过Openvpn建连，配置http代理服务。
 * 			 目的：数据加密，鉴权，高可用，限制IP访问
 */
public class StartHttpProxy extends StartService{

	private final static Logger logger = LoggerFactory.getLogger(StartHttpProxy.class);
	
	public StartHttpProxy(Proxy proxy) {
		super(proxy);
	}

	@Override
	protected boolean startService() throws Exception {
		List<String> ServerIPList = proxy.getServerIP();
		String pacUrl = proxy.getPacURL();
		String VirtualIP = proxy.getVirtualIP().get(0);
		int port = proxy.getPort().get(0);
		Openvpn openvpn = new Openvpn(proxy.getOpenvpnConfig());
		//HTTP代理实现类
		ProxyService proxyImp = null;
		//判断PAC模式还是全局模式
		if(pacUrl == null || pacUrl.trim().length() == 0) {
			proxyImp = new HttpGlobalProxy(VirtualIP, port);
		} else {
			proxyImp = new HttpPacProxy(VirtualIP, port, pacUrl);
		}
		boolean isConnect = false;
		for(String ServerIP : ServerIPList) {
			isConnect = isConnect | NetworkUtils.ping2(ServerIP, 3, 500);
			if(isConnect == true) {
				break;
			}
		}
		if(isConnect == false) {
			new MessageDialog("无法连接到接入服务器").show();
			logger.info("Cannot connect to OpenVPN server, no servers can be connected");
			return false;
		}
		if(proxy.isNeedAuthen() == true) {
			if(!new OpenVPNAuthDlg().isCurrentInput()) {
				logger.info("Directing quit or authentication operator problems");
				return false;
			}
		}
		try {
			int result = openvpn.startUp();
			if(result == 0 && isProxyAvailable(VirtualIP, port, ServiceUtils.getHttpProxyVerifyHost(proxy))) {
				proxyImp.startUp();
				getDetectListener().start();
				ProxyManager.getManager().addProxy(openvpn).addProxy(proxyImp);
				logger.info("Setting http proxy sucessful");
				return true;
			} else {
				logger.info("Cannot connect to proxy server, no servers can be used");
			}
		} catch (OpenVPNException e) {
			logger.warn("Error : " + e.toString());
			new MessageDialog(e.toString()).show();
		}
		proxyImp.kill();
		openvpn.kill();
		return false;
	}
	
	/**
	 * 初始化Http代理可用的监听器
	 */
	public DetectListener getDetectListener() {
		List<String> serverList = proxy.getProxyVerifyServer();
		String virtualIP = proxy.getVirtualIP().get(0);
		int port = proxy.getPort().get(0);
		DetectListener listener = DetectListener.getInstance().init();
		if(serverList != null && serverList.size() >= 1) {
			for(String server : serverList) {
				listener.adddetectServices(new HttpProxyListener(virtualIP, port, server));
			}
		} else {
			listener.adddetectServices(new HttpProxyListener(virtualIP, port, 
					ConfReader.getConfig().getDefaultHttpVerifyServer()));
		}
		//listener.adddetectServices(new PingListener(virtualIP, 500, false));
		listener.adddetectServices(new InetPingListener(virtualIP, 2000, 5000, false));
		return listener;
	}
	
	/**
	 * 探测代理可用
	 */
	public boolean isProxyAvailable(String ipAddress, int port, String domain) throws Exception{
		for(int i = 0; i < 5; i++) {
			if(HttpProxy.isHttpProxyAvailable(ipAddress, port, domain) == true) {
				return true;
			}
			Thread.sleep(2000);
		}
		return false;
	}

	public static ServiceFactory factory = new ServiceFactory() {	
		@Override
		public Service getService(Proxy proxy) {
			return new StartHttpProxy(proxy);
		}
	};
}
