package com.club203.service.sdnSocks5;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.detect.DetectListener;
import com.club203.detect.internal.InetPingListener;
import com.club203.detect.internal.TelnetListener;
import com.club203.dialog.MessageDialog;
import com.club203.dialog.OpenVPNAuthDlg;
import com.club203.exception.openvpn.OpenVPNException;
import com.club203.exception.socks5.ShadowsocksException;
import com.club203.proxy.ProxyManager;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.proxy.socks5.ShadowsocksProxy;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;
import com.club203.service.StartService;
import com.club203.utils.NetworkUtils;

/**
 * @author hehaoxing
 * 我们借助SDN/OSPF网络，加速接入代理服务器
 * 由于OpenVPN与Shadowsocks均需要秘钥，这里使用相同的秘钥。
 */
public class StartSocks5Proxy extends StartService {
	
	private final static Logger logger = LoggerFactory.getLogger(StartSocks5Proxy.class);

	public StartSocks5Proxy(Proxy proxy) {
		super(proxy);
	}

	@Override
	protected boolean startService() throws Exception {
		List<String> serverIPList = proxy.getServerIP();
		//接入点虚拟网卡IP，多个接入服务器提供统一的接入IP
		String acsVtlIP = proxy.getVirtualIP().get(0);//接入点
		String outVtlIP = proxy.getVirtualIP().get(1);//出口
		//OpenVPN配置文件
		String vpnfile = proxy.getOpenvpnConfig();
		//RestAPI
		String restURL = proxy.getRestURL();
		
		//检测接入点联通性
		boolean isConnect = false;
		for(String ServerIP : serverIPList) {
			isConnect = isConnect | NetworkUtils.ping2(ServerIP, 3, 500);
			if(isConnect == true) {
				break;
			}
		}
		//取用户名与密码
		String username = null;
		String passwd = null;
		if(proxy.isNeedAuthen()) {
			OpenVPNAuthDlg dialog = new OpenVPNAuthDlg();
			if(dialog.isCurrentInput()) {
				username = dialog.getUsername();
				passwd = dialog.getPasswd();
			} else {
				return false;
			}
		}
		
		//建立openvpn隧道后，配置出口路由出口，并开启四层代理
		Openvpn openvpn = new Openvpn(vpnfile);
		ShadowsocksProxy ssproxy = new ShadowsocksProxy(outVtlIP, username, passwd, restURL);
		try {
			int vpnStatus = openvpn.startUp();
			if(vpnStatus == 0) {
				//配置指向出口的路由表
				Runtime.getRuntime().exec("route add " + outVtlIP + " mask 255.255.255.255 " + acsVtlIP).waitFor();
				logger.info("Execute command: route add " + outVtlIP + " mask 255.255.255.255 " + acsVtlIP);
				int ssStatus = ssproxy.startUp();		
				//判断代理是否正常启动
				if(ssStatus == 0) {
					int port = ssproxy.getPort();
					DetectListener listener = DetectListener.getInstance().init()
															.adddetectServices(new InetPingListener(acsVtlIP, 2000, 5000, false))
															.adddetectServices(new InetPingListener(outVtlIP, 2000, 5000, false))
															.adddetectServices(new TelnetListener(outVtlIP, port, 2000, 5000));
					listener.start();
					ProxyManager.getManager().addProxy(openvpn).addProxy(ssproxy);
					logger.info("Setting http proxy sucessful");
					return true;
				} else {
					logger.info("Shadowsocks Proxy is not usable");
					Runtime.getRuntime().exec("route delete " + outVtlIP + " mask 255.255.255.255 " + acsVtlIP).waitFor();
					logger.info("Execute command: route delete " + outVtlIP + " mask 255.255.255.255 " + acsVtlIP);
				}
			}
		} catch (OpenVPNException e) {
			logger.warn("Error : " + e.toString());
			new MessageDialog(e.toString()).show();
		} catch (ShadowsocksException e) {
			logger.warn("Error : " + e.toString());
			new MessageDialog(e.toString()).show();
		}
		ssproxy.kill();
		openvpn.kill();
		logger.info("Cannot connect to proxy server, no servers can be used");
		return false;
	}
	
	public static ServiceFactory factory = new ServiceFactory() {	
		@Override
		public Service getService(Proxy proxy) {
			return new StartSocks5Proxy(proxy);
		}
	};
}
