package com.club203.service;

import com.club203.beans.Proxy;

public interface ServiceFactory {
	public Service getService(Proxy proxy);
}
