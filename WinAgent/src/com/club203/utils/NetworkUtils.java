package com.club203.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hehaoxing
 * 
 * 网络相关工具类
 */
public class NetworkUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(NetworkUtils.class);
	
	/****************************************Ping****************************************/
	/**
	 * 通过解析控制台命令检查通断
	 * @param ipAddress 目的服务器
	 * @param pingTimes 次数
	 * @param timeOut   超时时延
	 */
    public static boolean ping(String ipAddress, int pingTimes, int timeOut) {  
        BufferedReader in = null;  
        Runtime r = Runtime.getRuntime();
        String pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;  
        // 执行命令并获取输出  
        try {
            Process p = r.exec(pingCommand);   
            if (p == null) {    
                return false;   
            }
            // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数  
            in = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            int connectedCount = 0;   
            String line = null;   
            while ((line = in.readLine()) != null) {    
            	// 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真  
                connectedCount += getCheckResult(ipAddress, line); 
            }   
            return connectedCount == pingTimes;  
        } catch (Exception ex) {  
        	// 出现异常则返回假  
            ex.printStackTrace();   
            return false;  
        } finally {   
            try {    
                in.close();   
            } catch (IOException e) {    
                e.printStackTrace();   
            }  
        }
    }
    
    /**
     * 检查是否能够连接到服务器
     * 降低了相比ping, ping2仅要求收到第一个包即可
     */
    public static boolean ping2(String ipAddress, int pingTimes, int timeOut) {
    	BufferedReader in = null;  
        Runtime r = Runtime.getRuntime();
        String pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;  
        // 执行命令并获取输出  
        try {
            Process p = r.exec(pingCommand);   
            if (p == null) {    
                return false;   
            }
            // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数  
            in = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            String line = null;   
            while ((line = in.readLine()) != null) {    
            	// 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真  
            	if(getCheckResult(ipAddress, line) == 1) {
            		return true;
            	}
            }   
            return false;  
        } catch (Exception ex) {  
        	// 出现异常则返回假  
            return false;  
        } finally {   
            try {    
                in.close();   
            } catch (IOException e) {}  
        }
    }
    
    /**
     * 指定了源地址的Ping，用于检测网关是否可用
     * @param srcAddress 本地网卡IP
     * @param dstAddress 目的地址IP
     */
    public static boolean pingS(String srcAddress, String dstAddress, int pingTimes, int timeOut) {
    	BufferedReader in = null;  
        Runtime r = Runtime.getRuntime();
        String pingCommand = "ping -S " + srcAddress +" -n " + pingTimes  + " -w " + timeOut + " " + dstAddress;
        System.out.println(pingCommand);
        // 执行命令并获取输出  
        try {
            Process p = r.exec(pingCommand);   
            if (p == null) {    
                return false;   
            }
            // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数  
            in = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK")));
            String line = null;   
            while ((line = in.readLine()) != null) {    
            	// 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真  
            	System.out.println(line);
            	if(getCheckResult(dstAddress, line) == 1) {
            		return true;
            	}
            }   
            return false;  
        } catch (Exception ex) {  
        	// 出现异常则返回假  
            return false;  
        } finally {   
            try {    
                in.close();   
            } catch (IOException e) {}  
        }
    }
    
    /**
     * 使用InetAddress发送ICMP报文，作为Ping命令请求。相比直接调用命令行Ping，其更容易控制间隔。
     * 此函数调用需要系统权限，否则会向端口7发送echo导致出错。
     * 域名在本地解析，timeOut时间不包含本地域名解析，所以依然有可能出现Ping时间过长的问题。
     */
    public static boolean inetPing(String ipAddress, int timeOut) {
    	try {
			return InetAddress.getByName(ipAddress).isReachable(timeOut);
		} catch (IOException e) {
			return false;
		}
    }
    
    private static int getCheckResult(String ip, String line) {  
    	//正则匹配，包含对应一项即可
        Pattern pattern = Pattern.compile("((\\d+)ms)(\\s+)(TTL=(\\d+))", Pattern.CASE_INSENSITIVE);  
        Matcher matcher = pattern.matcher(line);  
        if(matcher.find()) {
        	logger.debug("detIP=" + ip + " delay=" + matcher.group(2) + "ms ttl=" + matcher.group(5));
        	return 1;
        }
        return 0; 
    }
    
    /****************************************Dig****************************************/
    
    /***
     * 用于检测当前域名是否能被解析，阻塞小于30秒
     * 
     * @param domainName 待解析的域名
     */
    public static boolean dig(String domainName) {
		Callable<String> callable = new Callable<String>() {
		@Override
		public String call() throws Exception {
			try {
				String hostip = InetAddress.getByName(domainName).getHostAddress();
				return hostip;
			} catch (UnknownHostException e) {
				return null;
			}
		}};
		FutureTask<String> future = new FutureTask<>(callable);
		new Thread(future).start();
		try {
			String hostAddress = future.get(3, TimeUnit.SECONDS);
			if(hostAddress != null) {
				logger.debug(domainName + " : The hosts IP address is " + hostAddress);
				return true;
			} else {
				logger.debug(domainName + " : The hosts IP address is not found");
				return false;
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			logger.debug(domainName + " : DNS resolution times out");
			return false;
		}
	}
	
    /****************************************端口检测****************************************/
    
	/*** 
	 * 检测本地端口占用(TCP限定)
     * true:already in using  false:not using  
     */  
    public static boolean isLoclePortUsing(int port) {  
        boolean flag = true;  
        try {  
            flag = isPortUsing("127.0.0.1", port);  
        } catch (Exception e) {  
        }  
        return flag;  
    }  
    /***
     *  检测远端端口占用 ，需要本地可以解析域名(TCP限定)
     *  true:already in using  false:not using  
     */  
    public static boolean isPortUsing(String host,int port) {  
        boolean flag = false;  
        InetAddress theAddress;
		try {
			theAddress = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			return flag;
		}  
        try {  
            Socket socket = new Socket(theAddress,port);  
            flag = true;  
            socket.close();
        } catch (IOException e) {  }  
        return flag;  
    }  
    
    /****************************************DNS配置****************************************/
    private static List<String> getInterfaceList() {
		String os = System.getProperty("os.name");  
		if(!os.toLowerCase().startsWith("win")){  
			return null;
		}  
		BufferedReader in = null;  
        Runtime r = Runtime.getRuntime();
        String getInterfaceCommand = "netsh interface show interface";  
        // 执行命令并获取输出  
        try {
            Process p = r.exec(getInterfaceCommand);  
            logger.info("Execute command: " + getInterfaceCommand);
            if (p == null) {    
                return null;   
            }
            //Win10下的获取ipconfig方法
            in = new BufferedReader(new InputStreamReader(p.getInputStream(), Charset.forName("GBK"))); 
            List<String> interfaceList = new ArrayList<>();
            String line = null;
            int lineNum = 0;
            while ((line = in.readLine()) != null) {        
        		lineNum++;
        		if(lineNum >= 4) {
        			String[] lineSplit = line.split("\\s+");
        			String interfaceName = "";
        			for(int i = 3; i < lineSplit.length; i++){
                		interfaceName += lineSplit[i];
                		interfaceName += " ";
                   	}
        			interfaceName = interfaceName.trim();
        			//可能造成问题,但一般是正确的
        			if(!interfaceName.startsWith("VMware Network Adapter")) {
        				interfaceList.add(interfaceName);
        			}
        		}
            }   
            interfaceList.remove(interfaceList.size() - 1);
            return interfaceList;  
        } catch (Exception ex) {  
            return null;  
        } finally {   
            try {    
                in.close();   
            } catch (IOException e) {    
                return null;   
            }  
        }
	}
	
    /**
     * 为主机配置DNS服务
     * 
     * @param dnsServer DNS服务器列表
     * @return boolean  是否生效
     * @throws
     */
	public static boolean setDNS(List<String> dnsServer) {
		String os = System.getProperty("os.name");  	
		if(!os.toLowerCase().startsWith("win")){  
			return false;
		}  
		if(dnsServer.size() == 0) {
			logger.info("Setting DNS server list.");
			return true;
		}
		logger.info("Starting setting DNS address");
		List<String> interfaceList = getInterfaceList();
		List<String> commandList = new ArrayList<>();
		for(String interfaceName : interfaceList) {
			int commandNum = 1;
			for(String dnsAddress : dnsServer) {
				if(commandNum == 1) {
					commandList.add("netsh interface ip set dns \"" + interfaceName +"\" static " + dnsAddress);
				}else {
					commandList.add("netsh interface ip add dns \"" + interfaceName +"\" " + dnsAddress);
				}
				commandNum++;
			}
		}
		//逐条执行命令
		for(String command : commandList) {
			try {
				Runtime.getRuntime().exec(command).waitFor();
				logger.info("Execute command: " + command);
			} catch (InterruptedException | IOException e) { }
		}
		logger.info("Setting DNS address successful");
		return true;
	}
	
	/**
	 * 重置DNS配置
	 */
	public static boolean resetDNS() {
		String os = System.getProperty("os.name");  
		if(!os.toLowerCase().startsWith("win")){  
			return false;
		}  
		logger.info("Starting resetting DNS address");
		List<String> interfaceList = getInterfaceList();
		List<String> commandList = new ArrayList<>();
		for(String interfaceName : interfaceList) {
			commandList.add("netsh interface ip set dns \"" + interfaceName +"\" dhcp");
		}
		for(String command : commandList) {
			try {
				Runtime.getRuntime().exec(command).waitFor();
				logger.info("Execute command: " + command);
			} catch (InterruptedException | IOException e) { }
		}
		logger.info("Resetting DNS address successful");
		return true;
	}
	
	public static void disableJVMDNSCache() {
		System.setProperty("sun.net.inetaddr.ttl", "0");
		System.setProperty("sun.net.inetaddr.negative.ttl", "0");  
		Security.setProperty("networkaddress.cache.ttl" , "0");
		Security.setProperty("networkaddress.cache.negative.ttl", "0");
	}
	
	public static boolean cleanDNSCache() {
		//清空了主机DNS缓存。
		try {
			Runtime.getRuntime().exec("ipconfig /flushdns").waitFor();
		} catch (InterruptedException | IOException e) {
			return false;
		}	
		return true;
	}
	
	/****************************************Telnet****************************************/
	
	private static TelnetClient client = new TelnetClient();
	/**
	 * 测试端口是否可以被telnet，相当于echo ping
	 * 
	 * @param ipAddress 目的服务器
	 * @param pingTimes 次数
	 * @param timeOut   超时时延
	 */
	public static boolean isTelnetable(String ipAddress, int port, int timeOut) {
		try {
			client.setDefaultTimeout(timeOut);
			client.connect(ipAddress, port);
			client.disconnect();
			logger.info(ipAddress + ":" + port + " : Remote server telnet success");
			return true;
		} catch (Exception e) {
			logger.info(ipAddress + ":" + port + " : Remote server telnet failed.");
			return false;
		}
	}
}
