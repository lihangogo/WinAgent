package com.club203.service.openvpn;

import java.util.ArrayList;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.config.GatewayReader;
import com.club203.dialog.MessageDialog;
import com.club203.dialog.OpenVPNAuthDlg;
import com.club203.exception.openvpn.OpenVPNException;
import com.club203.proxy.ProxyManager;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.service.Service;
import com.club203.service.ServiceFactory;
import com.club203.service.ServiceUtils;
import com.club203.service.StartService;
import com.club203.utils.NetworkUtils;
/**
 * 此类负责打开一个OpenVPN代理
 * @author hehaoxing
 * 
 * 1. 没有使用OpenVPN自带的两个参数：
 * 		在Windows系统下，由于DNS服务器建在校园网环境中，故需要通过setDNS()函数添加在校园网网卡上
 * 		不使用redirect-gateway def1参数：
 * 			不符合部分流量从本地释放这一要求，且服务器IP地址会直接暴露在路由表信息中
 * 		不使用dhcp-option DNS参数：Windows系统自身在多网卡环境下DNS服务器地址存在竞争关系，优先级与注册表项有关
 * 			仅仅为虚拟网卡推送DNS有时会失灵(推送所有可用网卡)
 * 		此类按照自己的策略，自行实现这两个参数。
 * 2. 公网探测维护状态
 * 		由于用于访问公网，需要探测对公网访问与DNS解析情况
 * 3. 使用永久路由
 * 		部分用户指定-p作为自身路由表会导致建连配置路由表失败，故清除与添加路由表均带上-p。
 * 4. 由于使用ping检测客户端可用性(而非UDP模拟发包)，请保证服务器程序随服务器启动。否则会建连操作会阻塞直至超时。
 */
public class StartOpenvpnProxy extends StartService {
	
	private final static Logger logger = LoggerFactory.getLogger(StartOpenvpnProxy.class);
	
	public StartOpenvpnProxy(Proxy proxy) {
		super(proxy);
	}
	
	@SuppressWarnings("unused")
	@Override
	protected boolean startService() throws Exception {
		String ProxyName = proxy.getProxyName();
		List<String> ServerIPList = proxy.getServerIP();
		String Gateway = GatewayReader.getDefaultGateway();
		String VirtualIP = proxy.getVirtualIP().get(0);
		
		Openvpn openvpn = new Openvpn(proxy.getOpenvpnConfig());
		boolean isConnect = false;
		for(String ServerIP : ServerIPList) {
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
		//装填路由表
		List<String> commandList = new ArrayList<>();
		//校园网内外的不同的连接方式
		if(proxy.isSchoolService() == true) {
			commandList.add("route add 10.0.0.0 mask 255.0.0.0 " + Gateway);
		} else {
			for(String ServerIP : ServerIPList) {
				commandList.add("route add " + OpenvpnUtils.ipEncrypt(ServerIP) + " mask 255.255.255.0 " + Gateway);
			}
		}
		commandList.add("route -p add 0.0.0.0 mask 0.0.0.0 " + VirtualIP);
		commandList.add("route -p delete 0.0.0.0 mask 0.0.0.0 " + Gateway);
		
		try {
			int result = openvpn.startUp();
			Runtime.getRuntime().exec("ipconfig /flushdns").waitFor();
			for(String command : commandList) {
				Runtime.getRuntime().exec(command).waitFor();
				logger.info("Execute command: " + command);
			}
			NetworkUtils.setDNS(proxy.getDnsServer());
			//检查可用
			String proxyVerifyServer = ServiceUtils.getProxyVerifyIP(proxy);
			String dnsVerifyServer = ServiceUtils.getDnsVerifyHost(proxy);
			if(result == 0 && waitForConnected(proxyVerifyServer, dnsVerifyServer) == true){
				logger.info("Connecting to OpenVPN server sucessful");
				OpenvpnUtils.getDetectListener(proxy).start();
				ProxyManager.getManager().addProxy(openvpn);
				return true;
			} else {
				logger.info("Cannot connect to OpenVPN server, rollback");
			}
		} catch(OpenVPNException e){
			logger.warn("Error : " + e.toString());
			new MessageDialog(e.toString()).show();
		};
		openvpn.kill();
		new Thread(()->NetworkUtils.resetDNS()).start();
		OpenvpnUtils.routeRollback(proxy);
		return false;
	}

	/**
	 * 等待连接建立完成并返回，提前配路由会导致路由表对应网卡错误
	 * @param ipaddress			用于探测Ping可用的IP地址
	 * @param dnsCheckDomain	用于探测DNS可用的域名
	 * @return 建立成功返回true，否则返回false
	 */
	public boolean waitForConnected(String ipaddress, String dnsCheckDomain){
		//连接建立完成之前修改路由表，有可能导致指向错误网关
		for(int i = 0; i < 3; i++){
			if(NetworkUtils.ping2(ipaddress, 3, 500) == true){
				for(int j = 0; j <= 1; j++) {
					if(NetworkUtils.dig(dnsCheckDomain) == true) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static ServiceFactory factory = new ServiceFactory() {	
		@Override
		public Service getService(Proxy proxy) {
			return new StartOpenvpnProxy(proxy);
		}
	};
}
