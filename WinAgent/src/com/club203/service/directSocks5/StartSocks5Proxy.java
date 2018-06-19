package com.club203.service.directSocks5;

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
 * 启动Socks5代理，这里指shadowsocks代理
 */
public class StartSocks5Proxy extends StartService{

	private final static Logger logger = LoggerFactory.getLogger(StartSocks5Proxy.class);

	public StartSocks5Proxy(Proxy proxy) {
		super(proxy);
	}

	@Override
	protected boolean startService() {
		List<String> serverIPList = proxy.getServerIP();
		String VirtualIP = proxy.getVirtualIP().get(0);
		String vpnfile = proxy.getOpenvpnConfig();
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
		
		//建立openvpn隧道后，开启四层代理
		Openvpn openvpn = new Openvpn(vpnfile);
		ShadowsocksProxy ssproxy = new ShadowsocksProxy(VirtualIP, username, passwd, restURL);
		try {
			//非0返回值均会抛出异常
			int status = openvpn.startUp();
			if(status == 0) {
				status = ssproxy.startUp();
				if(status == 0) {
					int ssport = ssproxy.getPort();
					//ProxyManager
					ProxyManager.getManager().addProxy(openvpn).addProxy(ssproxy);
					//Listener
					DetectListener listener = DetectListener.getInstance().init();
					listener.adddetectServices(new InetPingListener(VirtualIP, 2000, 5000, false))
							.adddetectServices(new TelnetListener(VirtualIP, ssport, 2000, 5000));
					listener.start();
					logger.info("Setting shadowsocks proxy sucessful");
					return true;
				} 
			}
		} catch (OpenVPNException e) {
			logger.warn("Error : " + e.toString());
			new MessageDialog(e.toString()).show();
		} catch (ShadowsocksException e) {
			logger.warn("Error : " + e.toString());
			new MessageDialog(e.toString()).show();
		}
		openvpn.kill();
		ssproxy.kill();
		return false;
	}

	public static ServiceFactory factory = new ServiceFactory() {	
		@Override
		public Service getService(Proxy proxy) {
			return new StartSocks5Proxy(proxy);
		}
	};
}
