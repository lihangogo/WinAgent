package com.club203.beans;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.club203.config.ConfReader;
import com.google.gson.Gson;

/**
 * 代理的POJO类，对应一种代理
 * 
 * @author hehaoxing
 * 
 * 通过支持多个客户端IP来保障高可用性
 * 若为保持代理的高可用性，需要相应服务器使用相同的配置
 * 如果要修改配置文件，则需要先修改这个pojo类
 * 
 * 不同的业务填写需要的字段，Json允许仅写入部分字段
 */
public class Proxy {
		//代理名称，拿name当ID简化操作。重名难以区分禁止
		private String ProxyName;
		//业务类型(对应实际业务)
		private int ServiceType;
		//代理实现方式(对应具体实现)
		private int ProxyType;	
		//服务器对应的IP地址
		private List<String> ServerIP;
		//服务器对端的虚拟IP地址，用于OpenVPN隧道
		private List<String> VirtualIP;
		//服务器代理四层端口，这里使用列表存储多个接口
		private List<Integer> Port;
		//客户端提供了帮助用户设置DNS的服务
		private List<String> DnsServer;
		
		//用于校验代理可用性的域名
		//用于校验代理可用性的域名，若为空则使用默认Server验证探测。连接验证仅使用第一项，探测使用全部项
		private List<String> ProxyVerifyServer;
		//用于校验DNS可用性的域名
		//用于校验DNS可用性的域名，若为空则使用默认Server验证探测。连接验证仅使用第一项，探测使用全部项
		private List<String> DnsVerifyServer;
		
		//对接入用户进行身份认证，借助OpenVPN服务端完成
		private boolean NeedAuthen;
		//OpenVPN配置文件名，一般与代理名称对应
		private String OpenvpnConfig;
		//PAC配置的HTTP链接，一般用于代理自动配置
		private String PacURL;
		//需要请求的RestAPI URL链接
		private String RestURL;

		public Proxy(String proxyName, int serviceType, int proxyType, List<String> serverIP, List<String> virtualIP,
				List<Integer> port, List<String> dnsServer, List<String> proxyVerifyServer,
				List<String> dnsVerifyServer, boolean needAuthen, String openvpnConfig, String pacURL, String restURL) {
			super();
			ProxyName = proxyName;
			ServiceType = serviceType;
			ProxyType = proxyType;
			ServerIP = serverIP;
			VirtualIP = virtualIP;
			Port = port;
			DnsServer = dnsServer;
			ProxyVerifyServer = proxyVerifyServer;
			DnsVerifyServer = dnsVerifyServer;
			NeedAuthen = needAuthen;
			OpenvpnConfig = openvpnConfig;
			PacURL = pacURL;
			RestURL = restURL;
		}

		public String getProxyName() {
			return ProxyName;
		}

		public void setProxyName(String proxyName) {
			ProxyName = proxyName;
		}

		public String getProxyType() {
			String[] ProxyTypeList = ConfReader.getConfig().getProxyType();
			if(this.ProxyType > 0 && this.ProxyType <= ProxyTypeList.length )
				return ProxyTypeList[this.ProxyType - 1];
			else
				return null;
		}
		
		public int getProxyTypeIndex() {
			return ProxyType;
		}

		public void setProxyType(String proxytype) {
			String[] ProxyTypeList = ConfReader.getConfig().getProxyType();
			for(int i = 0; i< ProxyTypeList.length; i++){
				if(proxytype.equals(ProxyTypeList[i])){
					this.ProxyType = i + 1;
					return;
				}
			}
			this.ProxyType = -1;
		}
			
		public void setProxyType(int proxytype) {
			this.ProxyType = proxytype;
		}
			
		public String getServiceType() {
			String[] ServiceTypeList = ConfReader.getConfig().getServiceType();
			if(this.ServiceType > 0 && this.ServiceType <= ServiceTypeList.length)
				return ServiceTypeList[this.ServiceType - 1];
			else
				return null;
		}
		
		public int getServiceTypeIndex() {
			return ServiceType;
		}

		public void setServiceType(String serviceType) {
			String[] ServiceTypeList = ConfReader.getConfig().getServiceType();
			for(int i = 0; i< ServiceTypeList.length; i++){
				if(serviceType.equals(ServiceTypeList[i])){
					this.ServiceType = i + 1;
					return;
				}
			}
			this.ServiceType = -1;
		}
			
		public void setServiceType(int serviceType) {
			ServiceType = serviceType;
		}

		public List<String> getServerIP() {
			return ServerIP;
		}

		public void setServerIP(List<String> serverIP) {
			ServerIP = serverIP;
		}

		public List<String> getVirtualIP() {
			return VirtualIP;
		}

		public void setVirtualIP(List<String> virtualIP) {
			VirtualIP = virtualIP;
		}

		public List<Integer> getPort() {
			return Port;
		}

		public void setPort(List<Integer> port) {
			Port = port;
		}

		public List<String> getDnsServer() {
			return DnsServer;
		}

		public void setDnsServer(List<String> dnsServer) {
			DnsServer = dnsServer;
		}

		public List<String> getProxyVerifyServer() {
			return ProxyVerifyServer;
		}

		public void setProxyVerifyServer(List<String> proxyVerifyServer) {
			ProxyVerifyServer = proxyVerifyServer;
		}

		public List<String> getDnsVerifyServer() {
			return DnsVerifyServer;
		}

		public void setDnsVerifyServer(List<String> dnsVerifyServer) {
			DnsVerifyServer = dnsVerifyServer;
		}

		public boolean isNeedAuthen() {
			return NeedAuthen;
		}

		public void setNeedAuthen(boolean needAuthen) {
			NeedAuthen = needAuthen;
		}

		public String getOpenvpnConfig() {
			return OpenvpnConfig;
		}

		public void setOpenvpnConfig(String openvpnConfig) {
			OpenvpnConfig = openvpnConfig;
		}

		public String getPacURL() {
			return PacURL;
		}

		public void setPacURL(String pacURL) {
			PacURL = pacURL;
		}

		public String getRestURL() {
			return RestURL;
		}

		public void setRestURL(String restURL) {
			RestURL = restURL;
		}

		public boolean isSchoolService() {
			String ServiceType = getServiceType();
			String[] SchoolServiceType = ConfReader.getConfig().getSchoolServiceType();
			for(int i = 0; i < SchoolServiceType.length; i++) {
				if(ServiceType.equals(SchoolServiceType[i])) {
					return true;
				}
			}
			return false;
		}

		/**
		 * 代理信息校验，为真即合法
		 */
		public boolean proxyCheck() {
			if(ServiceType < 1 || ServiceType > ConfReader.getConfig().getServiceType().length) {
				return false;
			}else if(ProxyType < 1 || ProxyType > ConfReader.getConfig().getProxyType().length) {
				return false;
			//熔断，没有内网虚拟地址也算正确
			}else {
				//端口号在合理的区间内
				if(Port != null) {
					for(Integer port : Port) {
						if(port < 1 || port > 65535) {
							return false;
						}
					}
				}
				//所有的ServiceIP都必须合法
				if(ServerIP != null) {
					for(String server : ServerIP) {
						if(isIpv4(server) == false) {
							return false;
						}
					}
				}
				//所有的VirtualIP都必须合法
				if(VirtualIP != null) {
					for(String server : VirtualIP) {
						if(isIpv4(server) == false) {
							return false;
						}
					}
				}
				//所有的DNS IP都必须合法
				if(DnsServer != null) {
					for(String server : DnsServer) {
						if(isIpv4(server) == false) {
							return false;
						}
					}
				}
				return true;
			}
		}

		public boolean isIpv4(String ipAddress) {
			if(ipAddress == null) {
				return false;
			}
	        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
	                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
	                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
	                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
	        Pattern pattern = Pattern.compile(ip);
	        Matcher matcher = pattern.matcher(ipAddress);
	        return matcher.matches();
	    }
		
		@Override
		public String toString() {
			Gson gson = new Gson();
			return gson.toJson(this);
		}
}
