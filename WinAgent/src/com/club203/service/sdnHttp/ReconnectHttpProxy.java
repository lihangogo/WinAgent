package com.club203.service.sdnHttp;

import com.club203.beans.Proxy;
import com.club203.service.ReconnectService;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;

/**
 * @author hehaoxing
 *
 */
public class ReconnectHttpProxy extends ReconnectService {
	
	public ReconnectHttpProxy(Proxy proxy) {
		super(proxy);
	}

	@Override
	protected boolean reconnectService() throws Exception {
		return false;
	}

	public static ServiceFactory factory = new ServiceFactory() {	
		@Override
		public Service getService(Proxy proxy) {
			return new ReconnectHttpProxy(proxy);
		}
	};
}
