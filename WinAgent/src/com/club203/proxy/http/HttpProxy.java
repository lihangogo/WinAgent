package com.club203.proxy.http;

import java.nio.charset.Charset;

import com.club203.utils.HttpUtils;
import com.registry.RegistryKey;
import com.registry.RegistryValue;
import com.registry.ValueType;

/**
 * Win7/Win10借助配置注册表配置Http代理
 * 
 * @author hehaoxing
 * 
 * 注意需要在JDK的lib文件添加lib库支持
 * 此类用于读取注册表项，操纵注册表实现代理自动配置与取消
 * 在建立了OpenVPN隧道后再次配置HTTP代理可以做一部分数据加密
 * 
 * 注：注册表的编码格式与Java Unicode存在区别，尽管都是双字节编码。Java Unicode会在头部补负数，而注册表会在后面补0，其他均相同。
 */
public class HttpProxy {
	
	private static RegistryKey currentUser = RegistryKey.getRootKeyForIndex(RegistryKey.HKEY_CURRENT_USER_INDEX);
	private static RegistryKey proxyKey = new RegistryKey(currentUser, "\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\");
	private static RegistryKey proxyConnectKey = new RegistryKey(currentUser, "\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\\Connections\\");
	
	/**
	 * 设置代理可用
	 */
	private static void setProxyEnable() {
		if(proxyKey.hasValues()) {   
			RegistryValue proxyEnable = proxyKey.getValue("ProxyEnable");
			proxyEnable.setByteData(intToBytes(1));
		} 
	}
	
	/**
	 * 设置代理不可用
	 */
	private static void setProxyDisable() {
		if(proxyKey.hasValues()) {   
			RegistryValue proxyEnable = proxyKey.getValue("ProxyEnable");
			proxyEnable.setByteData(intToBytes(0));
		} 
	}
	
	/**
	 * 设置代理地址
	 */
	private static void setProxyAddress(String ipAddress, int port) throws Exception {
		if(proxyKey.hasValues()) {   
			RegistryValue proxyServer = proxyKey.getValue("ProxyServer");
			//特定的转码格式，可能由于系统出错
			byte[] tmp = (ipAddress + ":" + String.valueOf(port)).getBytes(Charset.forName("UTF-8"));
			byte[] res = new byte[(tmp.length + 1) * 2];
			//API中对应的编码格式与Java编码格式有区别
			for(int i = 0; i < tmp.length; i++) {
				res[i * 2] = tmp[i];
				res[i * 2 + 1] = 0;
			}
			proxyServer.setByteData(res);
		} 
	}
	
	/**
	 * 设置注册表内网禁用代理
	 */
	private static void setProxyOverride() {
		if(proxyKey.hasValues()) {   
			RegistryValue proxyOverride = proxyKey.getValue("ProxyOverride");
			//特定的转码格式，可能由于系统出错
			String localSeg = "localhost;127.*;10.*;172.16.*;172.17.*;172.18.*;172.19.*;172.20.*;172.21.*;172.22" + 
				     		  ".*;172.23.*;172.24.*;172.25.*;172.26.*;172.27.*;172.28.*;172.29.*;172.30.*;172.31" + 
				     		  ".*;172.32.*;192.168.*" + ";<local>";
			byte[] tmp = (localSeg).getBytes(Charset.forName("UTF-8"));
			byte[] res = new byte[(tmp.length + 1) * 2];
			//API中对应的编码格式与Java编码格式有区别
			for(int i = 0; i < tmp.length; i++) {
				res[i * 2] = tmp[i];
				res[i * 2 + 1] = 0;
			}
			proxyOverride.setByteData(res);
		} 
	}
	
	/**
	 * 设置注册表项AutoConfigURL
	 */
	private static void setProxyAutoConfigURL(String configURL) {
		if(proxyKey.hasValues()) {   
			RegistryValue autoConfigURL = proxyKey.getValue("AutoConfigURL");
			if (autoConfigURL == null) {
				autoConfigURL = proxyKey.newValue("AutoConfigURL", ValueType.REG_SZ);
			}
			byte[] tmp = configURL.getBytes(Charset.forName("UTF-8"));
			byte[] res = new byte[(tmp.length + 1) * 2];
			//API中对应的编码格式与Java自带的编码格式区别极大
			for(int i = 0; i < tmp.length; i++) {
				res[i * 2] = tmp[i];
				res[i * 2 + 1] = 0;
			}
			autoConfigURL.setByteData(res);
		} 
	}
	
	/**
	 * 移除注册表项AutoConfigURL
	 */
	private static void removeAutoConfigURL() {
		if(proxyKey.hasValues()) {   
			RegistryValue autoConfigURL = proxyKey.getValue("AutoConfigURL");
			if(autoConfigURL != null) {
				proxyKey.deleteValue(autoConfigURL);
			}
		}
	}
	
	/**
	 * 配置PAC默认代理注册表项：
	 * 	       相当于手动刷新一次代理项
	 * @param defaultConnectionSetting 注册表信息
	 */
	private static void setConnectionSetting(byte[] defaultConnectionSetting) {
		if(proxyConnectKey.hasValues()) {
			RegistryValue connectSetting = proxyConnectKey.getValue("DefaultConnectionSettings");
			if(connectSetting != null) {
				new Thread(() -> {
					//关闭代理，自动刷新重置状态
					byte[] stopConnectionSetting = getStopConnectionSettingsValue();
					stopConnectionSetting[8] = (byte) (stopConnectionSetting[8] | 0x08);
					connectSetting.setByteData(stopConnectionSetting);
					//打开自动刷新开关，置入配置
					defaultConnectionSetting[8] = (byte) (defaultConnectionSetting[8] | 0x08);
					connectSetting.setByteData(defaultConnectionSetting);
					try {
						Thread.sleep(3000);
						Runtime.getRuntime().exec("ipconfig /flushdns").waitFor();
					} catch (Exception e) { }
					//关闭自动刷新开关
					defaultConnectionSetting[8] = (byte) (defaultConnectionSetting[8] & 0x07);
					connectSetting.setByteData(defaultConnectionSetting);
				}).start();
			}
		}
	}
	
	/**
	 * 获取PAC局部代理的DefaultConnectionSettings
	 */
	private static byte[] getPacConnectionSettingsValue(String ipAddress, int port, String autoConfigURL) {
		return getConnectionSettingsValue(ipAddress, port, autoConfigURL, 0);
	}
	
	/**
	 * 获取全局代理的DefaultConnectionSettings
	 */
	private static byte[] getGlobalConnectionSettingsValue(String ipAddress, int port) {
		return getConnectionSettingsValue(ipAddress, port, null, 1);
	}
	
	/**
	 * 获取关闭代理的DefaultConnectionSettings
	 */
	private static byte[] getStopConnectionSettingsValue() {
		return getConnectionSettingsValue(null, 0, null, -1);
	}
	
	/**
	 * 构造所需的DefaultConnectionSettings项的字节数组
	 * 		Windows不同代理的实现：
	 * 			op = 0 ：使用PAC局部代理
	 * 			op = 1 ：使用全局代理
	 * 			op = -1：关闭代理
	 * 		为了保证在Windows10操作系统下的可用性，请引用PAC文件的Http链接以免出错
	 * 
	 * @param ipAddress 		Http代理服务器地址
	 * @param port 				Http代理服务端口
	 * @param autoConfigURL		PAC链接
	 * @param op				对应的操作
	 */
	private static byte[] getConnectionSettingsValue(String ipAddress, int port, String autoConfigURL, int op) {
		//注册表项DefaultConnectionSettings的长度
		byte[] res = new byte[1194 * 8];
		//操作数指定不明确
		if(op < -1 || op > 1) {
			return null;
		}
		//index用来标注当前位置
		int index = 0;
		//1. 第一位赋值为0x46
		res[index] = 0x46;
		res[4] = 0x01;
		//2. 跳过Internet Tools字段(第4位为操作计数值)，长度为7(这里有改的必要)
		index += 8;
		/*3. 设置代理选项：
		 *	开关(switcher): 主要代表IE设置中复选框的选中情况，以下是所有可用的值：
		 *	0F全部开启(ALL)						01全部禁用(Off)
		 *	03使用代理服务器(ProxyOnly)			05使用自动脚本(PacOnly)
		 *	07使用脚本和代理(ProxyAndPac)		09打开自动检测设置(D)
		 *	0B打开自动检测并使用代理(DIP)			0D打开自动检测并使用脚本(DS)
		 *
		 *  打开自动刷新只要或0x08即可
		 *	局部代理：07，仅PAC列表中URL被配置了代理，此种配置可用于多种服务器。
		 *  全局代理：03，即设置全局代理。
		 *  关闭代理：01，即关闭代理
		 */
		switch (op) {
			case -1:
				res[index] = 0x01;
				break;
			case 0:
				res[index] = 0x05;
				break;
			case 1:
				res[index] = 0x03;
			default:
				break;
		}
		//4. 跳过全为0的字段，长度为3。(在这里，变量与变量之间的分隔符长度均为3)
		index += 4;
		//5. 设置IP：端口字段的长度(传递参数)，长度为3的间隔符，IP地址与端口号byte数组化
		//   关闭代理时重新设置为：127.0.0.1:8080
		if(op == -1) {
			ipAddress = "127.0.0.1";
			port = 8080;
		}
		byte[] addr = (ipAddress + ":" + String.valueOf(port)).getBytes();
		res[index] = new Integer(addr.length).byteValue();
		index += 4;
		for(int i = 0; i < addr.length; i++) {
			res[index] = addr[i];
			index++;
		}
		//6. 下一位附加信息的长度(附加信息为类似)，长度为3的间隔符，所有的附加信息
		String addistr = "localhost;127.*;10.*;172.16.*;172.17.*;172.18.*;172.19.*;172.20.*;172.21.*;172.22" + 
					     ".*;172.23.*;172.24.*;172.25.*;172.26.*;172.27.*;172.28.*;172.29.*;172.30.*;172.31" + 
					     ".*;172.32.*;192.168.*" + ";<local>";
		byte[] addi = addistr.getBytes();
		res[index] = new Integer(addi.length).byteValue();
		index += 4;
		for(int i = 0; i < addi.length; i++) {
			res[index] = addi[i];
			index++;
		}
		//7. 添加附加信息，一说此位为感叹号，一说为代理长度，然后添加长度为3的间隔符，然后是代理文件位置
		//   当且仅当PAC代理模式下需要
		if(op == 0) {
			byte[] acURL = autoConfigURL.getBytes(); 
			res[index] = new Integer(addi.length).byteValue();
			index += 4;
			for(int i = 0; i < acURL.length; i++) {
				res[index] = acURL[i];
				index++;
			}
		}
		return res;
	}
	
	/**
	 * 将byte数组输出为十六进制字符串，测试用
	 */
	@SuppressWarnings("unused")
	private static String bytesToHex(byte[] bytes) {  
		final char[] hexArray = "0123456789ABCDEF".toCharArray(); 
	    char[] hexChars = new char[bytes.length * 3];  
	    for (int j = 0; j < bytes.length; j++ ) {  
	        int v = bytes[j] & 0xFF;  
	        hexChars[j * 3] = hexArray[v >>> 4];  
	        hexChars[j * 3 + 1] = hexArray[v & 0x0F];  
	        hexChars[j * 3 + 2] = ' ';
	    }  
	    return new String(hexChars);  
	} 
	
	/**
	 * 将int转换为byte数组
	 */
	private static byte[] intToBytes(int value) {   
	    byte[] src = new byte[4];  
	    src[3] = (byte) ((value>>24) & 0xFF);  
	    src[2] = (byte) ((value>>16) & 0xFF);  
	    src[1] = (byte) ((value>>8) & 0xFF);    
	    src[0] = (byte) (value & 0xFF);                  
	    return src;   
	}  
		
	/**
	 * 打开PAC局部代理配置：
	 * 	       先关闭代理后打开代理的目的是为了刷新
	 * @param ipAddress 		代理服务器IP
	 * @param port				代理服务器端口
	 * @param autoConfigURL		PAC文件对应的URL
	 */
	public static void startpacProxy(String ipAddress, int port, String autoConfigURL) throws Exception {
		setProxyEnable();
		setProxyOverride();
		setProxyAddress(ipAddress, port);
		setProxyAutoConfigURL(autoConfigURL);
		setConnectionSetting(getPacConnectionSettingsValue(ipAddress, port, autoConfigURL));
	}
	
	/**
	 * 打开全局代理，包含刷新操作
	 * @param ipAddress 		代理服务器IP
	 * @param port				代理服务器端口
	 */
	public static void startglobalProxy(String ipAddress, int port) throws Exception {
		setProxyEnable();
		setProxyOverride();
		setProxyAddress(ipAddress, port);
		setConnectionSetting(getGlobalConnectionSettingsValue(ipAddress, port));
	}
	
	/**
	 * 测试HTTP代理是否可用，规避Java在本地完成域名解析导致得到错误的信息
	 * 不使用URLConnection/HttpClient
	 * 
	 * @param ipAddress 		代理服务器IP
	 * @param port				代理服务器端口
	 * @param domain			被用于探测的域名
	 */
	public static boolean isHttpProxyAvailable(String ipAddress, int port, String domain) {
		if(domain.startsWith("http://")) {
			domain = domain.substring(7);
			try {
				if(HttpUtils.accessWithHttpProxy(ipAddress, port, domain, false) != -1) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		} else if(domain.startsWith("https://")) {
			domain = domain.substring(8);
			try {
				if(HttpUtils.accessWithHttpProxy(ipAddress, port, domain, true) != -1) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		} else {
			try {
				if(HttpUtils.accessWithHttpProxy(ipAddress, port, domain, true) != -1 || 
						HttpUtils.accessWithHttpProxy(ipAddress, port, domain, false) != -1) {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return false;
		
	}
	
	/**
	 * 关闭PAC代理配置
	 */
	public static void stopProxy() throws Exception{
		setProxyDisable();
		setProxyAddress("127.0.0.1", 8080);
		removeAutoConfigURL();
		setConnectionSetting(getStopConnectionSettingsValue());
	}

}
