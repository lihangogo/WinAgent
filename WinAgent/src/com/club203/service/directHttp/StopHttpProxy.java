package com.club203.service.directHttp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.detect.DetectListener;
import com.club203.proxy.ProxyManager;
import com.club203.proxy.ProxyService;
import com.club203.proxy.http.HttpGlobalProxy;
import com.club203.proxy.http.HttpPacProxy;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;
import com.club203.service.StopService;

/**
 * @author hehaoxing
 * 关闭HTTP代理，关闭时进行了简单的检查(后续可以补全)
 */
public class StopHttpProxy extends StopService{

	private final static Logger logger = LoggerFactory.getLogger(StopHttpProxy.class);
	
	public StopHttpProxy(Proxy proxy) {
		super(proxy);
	}

	@Override
	protected boolean stopService() throws Exception {
		ProxyManager manager = ProxyManager.getManager();
		logger.info(manager.toString());
		if(manager.getSize() == 2) {
			Openvpn openvpn = (Openvpn)manager.getFirstProxy(Openvpn.class);
			ProxyService httpProxy = null;
			String pacUrl = proxy.getPacURL();
			if(pacUrl == null || pacUrl.trim().length() == 0) {
				httpProxy = manager.getFirstProxy(HttpGlobalProxy.class);
			} else {
				httpProxy = manager.getFirstProxy(HttpPacProxy.class);
			}
			if(openvpn != null && httpProxy != null) {
				openvpn.kill();
				httpProxy.kill();
				manager.removeAllProxy();
				DetectListener.getInstance().cancel();
				logger.info("Stopping http proxy sucessful");
				return true;
			}
		}
		return false;
	}
	
	public static ServiceFactory factory = new ServiceFactory() {	
		@Override
		public Service getService(Proxy proxy) {
			return new StopHttpProxy(proxy);
		}
	};
}
