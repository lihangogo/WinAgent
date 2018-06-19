package com.club203.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取当前机器的网关
 * @author hehaoxing
 */
public class GatewayReader {
	//读取优先级最高的网关地址
	private static String defaultGateWay = null;
	//当前网段内的首个IP地址作为网关，推测得出的其他网关地址
	private static List<String> anotherGateWay = null;
	
	private final static Logger logger = LoggerFactory.getLogger(GatewayReader.class);
	
	private GatewayReader() {
		super();
	}
	
	public static String getDefaultGateway() {
		if(defaultGateWay == null) {
			new GatewayReader().readGateway();
		}
		return defaultGateWay;
	}
	
	public static List<String> getAnotherGateway() {
		if(anotherGateWay == null) {
			new GatewayReader().readGateway();
		}
		return anotherGateWay;
	}

	private void readGateway(){
		try {
			logger.info("Start reading gateway IP address");
			Process process = Runtime.getRuntime().exec("route print -4");
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
			String line = null;
			int maxHop = Integer.MAX_VALUE;
			anotherGateWay = new LinkedList<>();
			while ((line = br.readLine()) != null){
				//取跃点数最小的默认路由对应的网关作为默认网关，读取时忽略默认路由与数据链路层信息
				line = line.trim();
				String[] seg = line.split("\\s+");
				if(seg.length >= 5 && seg.length <= 8){
					String gw = line.split("\\s+")[2];
					if(isIpv4(gw)){
						anotherGateWay.add(gw);
						//默认网关在0.0.0.0里找跃点最小的，路由表中"On link"则网关位置
						if(line.indexOf("0.0.0.0") != -1){
							int hop = Integer.parseInt(line.split("\\s+")[4]);
							if(hop < maxHop) {
								maxHop = hop;
								defaultGateWay = gw;
							}
						}
					}
				} 
			}	
			anotherGateWay.remove(defaultGateWay);
			if(defaultGateWay == null) {
				JOptionPane.showMessageDialog(null, "错误：主机未接入网络", "错误", 0);
				System.exit(0);
			}
			anotherGateWay.remove(defaultGateWay);
			logger.info("Get default gateway IP address: " + defaultGateWay + " hop: " + maxHop);
			for(String gateway : anotherGateWay) {
				logger.info("Get another gateway IP address: " + gateway);
			}
			logger.info("Reading gateway IP address success");
		} catch (IOException e) {
			defaultGateWay = null;
			anotherGateWay = null;
			logger.error("Cannot get gateway IP address");
			System.exit(0);
		}
	}
	
	private boolean isIpv4(String ipAddress) {
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
	
}
