package com.club203.service.sdnHttp;

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
 * 借助SDN网络，加速客户端与代理服务器之间的访问；PAC用于分流
 * 
 * 启动HTTP PAC代理，使用接入点作为虚拟网关，使用出口服务器作为代理服务器
 * 与directHttpPac的区别：OpenVPN接入点与代理接入点不在用一个点上
 * 例：接入北京-洛杉矶线路，借助PAC与HTTP代理完成分流
 */
public class StartHttpProxy extends StartService {

	private final static Logger logger = LoggerFactory.getLogger(StartHttpProxy.class);
	
	public StartHttpProxy(Proxy proxy) {
		super(proxy);
	}

	@Override
	protected boolean startService() throws Exception {
		List<String> serverIPList = proxy.getServerIP();
		String pacUrl = proxy.getPacURL();
		//接入点虚拟网卡IP，多个接入服务器提供统一的接入IP
		String acsVtlIP = proxy.getVirtualIP().get(0);//接入点
		String outVtlIP = proxy.getVirtualIP().get(1);//出口
		//HTTP服务器代理端口
		int port = proxy.getPort().get(0);
		Openvpn openvpn = new Openvpn(proxy.getOpenvpnConfig());
		//HTTP代理实现类
		ProxyService proxyImp = null;
		if(pacUrl == null || pacUrl.trim().length() == 0) {
			proxyImp = new HttpGlobalProxy(outVtlIP, port);
		} else {
			proxyImp = new HttpPacProxy(outVtlIP, port, pacUrl);
		}
		
		boolean isConnect = false;
		for(String ServerIP : serverIPList) {
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
			if(result == 0) {
				//配置指向出口的路由表
				Runtime.getRuntime().exec("route add " + outVtlIP + " mask 255.255.255.255 " + acsVtlIP).waitFor();
				logger.info("Execute command: route add " + outVtlIP + " mask 255.255.255.255 " + acsVtlIP);
				proxyImp.startUp();		
				//判断代理是否可用
				if(isProxyAvailable(outVtlIP, port, ServiceUtils.getHttpProxyVerifyHost(proxy))) {
					getDetectListener()
						//.adddetectServices(new PingListener(acsVtlIP, 500, false))
						//.adddetectServices(new PingListener(outVtlIP, 500, false))
						.adddetectServices(new InetPingListener(acsVtlIP, 2000, 5000, false))
						.adddetectServices(new InetPingListener(outVtlIP, 2000, 5000, false))
						.start();
					ProxyManager.getManager().addProxy(openvpn).addProxy(proxyImp);
					logger.info("Setting http proxy sucessful");
					return true;
				} else {
					logger.info("HTTP Proxy is not usable");
					Runtime.getRuntime().exec("route delete " + outVtlIP + " mask 255.255.255.255 " + acsVtlIP).waitFor();
					logger.info("Execute command: route delete " + outVtlIP + " mask 255.255.255.255 " + acsVtlIP);
				}	
			}
		} catch (OpenVPNException e) {
			logger.warn("Error : " + e.toString());
			new MessageDialog(e.toString()).show();
		}
		proxyImp.kill();
		openvpn.kill();
		logger.info("Cannot connect to proxy server, no servers can be used");
		return false;
	}
	
	/**
	 * 初始化Http代理可用的监听器
	 */
	public DetectListener getDetectListener() {
		List<String> serverList = proxy.getProxyVerifyServer();
		//选择出口IP作为代理检测端口
		String outVtlIP = proxy.getVirtualIP().get(1);
		int port = proxy.getPort().get(0);
		DetectListener listener = DetectListener.getInstance().init();
		if(serverList != null && serverList.size() >= 1) {
			for(String server : serverList) {
				listener.adddetectServices(new HttpProxyListener(outVtlIP, port, server));
			}
		} else {
			listener.adddetectServices(new HttpProxyListener(outVtlIP, port, 
					ConfReader.getConfig().getDefaultHttpVerifyServer()));
		}
		return listener;
	}
	
	/**
	 * 探测HTTP代理可用
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
