package com.club203.beans;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import com.club203.service.Service;

/**
 * 将各种依赖关系写入配置文件，对应Spring配置文件
 * @author hehaoxing
 */
public class Configuration {
	
	private String[] ServiceType;
	private String[] ProxyType;
	private String[] SchoolServiceType;
	private HashMap<String, Class<? extends Service>> StartList;
	private HashMap<String, Class<? extends Service>> StopList;
	private HashMap<String, Class<? extends Service>> ReconnectList;
	
	public Configuration() {
		super();
	}

	public String[] getServiceType() {
		return ServiceType;
	}

	public void setServiceType(String[] serviceType) {
		ServiceType = serviceType;
	}

	public String[] getProxyType() {
		return ProxyType;
	}

	public void setProxyType(String[] proxyType) {
		ProxyType = proxyType;
	}

	public String[] getSchoolServiceType() {
		return SchoolServiceType;
	}

	public void setSchoolServiceType(String[] schoolServiceType) {
		SchoolServiceType = schoolServiceType;
	}

	public HashMap<String, Class<? extends Service>> getStartList() {
		return StartList;
	}

	@SuppressWarnings("unchecked")
	public void setStartList(HashMap<String, String> startList) {
		StartList = new HashMap<>();
		for(Entry<String, String> impl : startList.entrySet()) {
			String key = impl.getKey();
			String value = impl.getValue();
			try {
				this.StartList.put(key, (Class<? extends Service>) Class.forName(value));
			} catch (ClassNotFoundException e) { 
				System.exit(0);
			}
		}
	}

	public HashMap<String, Class<? extends Service>> getStopList() {
		return StopList;
	}

	@SuppressWarnings("unchecked")
	public void setStopList(HashMap<String, String> stopList) {
		StopList = new HashMap<>();
		for(Entry<String, String> impl : stopList.entrySet()) {
			String key = impl.getKey();
			String value = impl.getValue();
			try {
				this.StopList.put(key, (Class<? extends Service>) Class.forName(value));
			} catch (ClassNotFoundException e) { 
				System.exit(0);
			}
		}
	}

	public HashMap<String, Class<? extends Service>> getReconnectList() {
		return ReconnectList;
	}

	@SuppressWarnings("unchecked")
	public void setReconnectList(HashMap<String, String> reconnectList) {
		ReconnectList = new HashMap<>();
		for(Entry<String, String> impl : reconnectList.entrySet()) {
			String key = impl.getKey();
			String value = impl.getValue();
			try {
				this.ReconnectList.put(key, (Class<? extends Service>) Class.forName(value));
			} catch (ClassNotFoundException e) { 
				System.exit(0);
			}
		}
	}

	@Override
	public String toString() {
		return "Config [ServiceType=" + Arrays.toString(ServiceType) + ", ProxyType=" + Arrays.toString(ProxyType)
				+ ", SchoolServiceType=" + Arrays.toString(SchoolServiceType) + ", StartList=" + StartList
				+ ", StopList=" + StopList + "]";
	}
	
}
