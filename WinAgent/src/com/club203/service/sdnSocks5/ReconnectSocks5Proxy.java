package com.club203.service.sdnSocks5;

import com.club203.beans.Proxy;
import com.club203.service.ReconnectService;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;

/**
 * @author hehaoxing
 *
 */
public class ReconnectSocks5Proxy extends ReconnectService {

	public ReconnectSocks5Proxy(Proxy proxy) {
		super(proxy);
	}

	@Override
	protected boolean reconnectService() throws Exception {
		return false;
	}
	
	public static ServiceFactory factory = new ServiceFactory() {	
		@Override
		public Service getService(Proxy proxy) {
			return new ReconnectSocks5Proxy(proxy);
		}
	};
}
