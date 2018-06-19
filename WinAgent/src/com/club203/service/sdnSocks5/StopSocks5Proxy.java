package com.club203.service.sdnSocks5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.detect.DetectListener;
import com.club203.proxy.ProxyManager;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.proxy.socks5.ShadowsocksProxy;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;
import com.club203.service.StopService;

/**
 * @author hehaoxing
 *
 */
public class StopSocks5Proxy extends StopService {
	
	private final static Logger logger = LoggerFactory.getLogger(StopSocks5Proxy.class);

	public StopSocks5Proxy(Proxy proxy) {
		super(proxy);
	}

	@Override
	protected boolean stopService() throws Exception {
		ProxyManager manager = ProxyManager.getManager();
		logger.info(manager.toString());
		if(manager.getSize() == 2) {
			Openvpn openvpn = (Openvpn)manager.getFirstProxy(Openvpn.class);
			ShadowsocksProxy shadowsocks = (ShadowsocksProxy)manager.getFirstProxy(ShadowsocksProxy.class);
			if(openvpn != null && shadowsocks != null) {
				String acsVtlIP = proxy.getVirtualIP().get(0);//接入点
				String outVtlIP = proxy.getVirtualIP().get(1);//出口
				Runtime.getRuntime().exec("route delete " + outVtlIP + " mask 255.255.255.255 " + acsVtlIP).waitFor();
				logger.info("Execute command: route delete " + outVtlIP + " mask 255.255.255.255 " + acsVtlIP);
				openvpn.kill();
				shadowsocks.kill();
				manager.removeAllProxy();
				DetectListener.getInstance().cancel();
				logger.info("Stopping shadowsocks proxy sucessful");
				return true;
			}
		}
		return false;
	}

	public static ServiceFactory factory = new ServiceFactory() {	
		@Override
		public Service getService(Proxy proxy) {
			return new StopSocks5Proxy(proxy);
		}
	};
}
