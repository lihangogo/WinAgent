package com.club203.detect.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.config.GatewayReader;
import com.club203.detect.DetectService;

/**
 * 用于心跳连接的Ping，调用控制台完成
 * 由于调用了Windows API，不能设置时间间隔
 * 需要指定是否是IP层代理，以清理物理网卡推送默认路由
 */
public class PingListener implements DetectService {
	
	private String ipAddress;
	private int timeOut;
	private boolean isIpProxy;
	
	private Process process = null;
	private Thread executor = null;
	private volatile AtomicInteger count = new AtomicInteger(0);
	
	private static int THRESHOLD = 30;
	private final static Logger logger = LoggerFactory.getLogger(PingListener.class);
	
	public PingListener(String ipAddress, int timeOut) {
		this.ipAddress = ipAddress;
		this.timeOut = timeOut;
		this.isIpProxy = true;
	}
	
	public PingListener(String ipAddress, int timeOut, boolean isIpProxy) {
		this.ipAddress = ipAddress;
		this.timeOut = timeOut;
		this.isIpProxy = isIpProxy;
	}
	
	@Override
	public void start() {
		logger.info("Starting detecting ping: " + ipAddress);
		String pingCommand = "ping" + " -t -w " + timeOut + " " + ipAddress;  
        try {
			process = Runtime.getRuntime().exec(pingCommand);
		} catch (IOException e) { }
        
        //多网卡环境下定期清理网关，避免反复重新建连(仅针对IP层代理)
		String defaultGateway = GatewayReader.getDefaultGateway();
		List<String> anotherGateway = GatewayReader.getAnotherGateway();
		List<String> holdCommandList = new ArrayList<>();
		if(isIpProxy) {
			holdCommandList.add("route delete 0.0.0.0 mask 0.0.0.0 " + defaultGateway);
			for(String gateway : anotherGateway) {
				holdCommandList.add("route delete 0.0.0.0 mask 0.0.0.0 " + gateway);
			}
		}
        //可重入
		count.set(0);
        executor = new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));  
				Pattern pattern = Pattern.compile("((\\d+)ms)(\\s+)(TTL=(\\d+))", Pattern.CASE_INSENSITIVE); 
				//前几行一般没有什么有用的信息
				String line = null;
				Matcher matcher = null;
				try {
					while((line = in.readLine()) != null && !Thread.currentThread().isInterrupted()) {
						matcher = pattern.matcher(line);  
						if(matcher.find()) {
						    count.set(0);
						    logger.info("Ping: dstIP=" + ipAddress + " delay=" + matcher.group(2) + "ms ttl=" + matcher.group(5));
						} else {
						    count.getAndIncrement();
						    logger.info("Ping: dstIP:" + ipAddress + ", invaild information or connection failed");
						}
						if (count.get() > THRESHOLD) {
							return;
						} else if (count.get() >= THRESHOLD / 4 && count.get() % 10 == 0 && isIpProxy == true){
							//使用了永久路由还是有问题，需要清理推送网关(IP层代理，网关被物理网关覆盖的情况)
							for(String command : holdCommandList) {
								logger.info("Try to recover from disconnection");
								try {
									Runtime.getRuntime().exec(command).waitFor();
									logger.info("Execute: " + command);
								} catch (InterruptedException | IOException e) { 
									count.getAndIncrement();
									break;
								}
							}
						}
					}
				} catch (IOException e) {
				} finally {
					logger.info("Stopping detecting ping: " + ipAddress);
					count.set(Integer.MAX_VALUE / 2);
					if(process != null)
						process.destroy();
				}
			}
		});
        executor.start();
	}
	
	@Override
	public void cancel() {
		//强制退出
		count.set(Integer.MAX_VALUE / 2);
		if(process != null) {
			logger.info("Detecting pinging " + ipAddress + " is cancelled");
			process.destroy();
		}
		if(executor != null) {
			executor.interrupt();
		}
	}
	
	@Override
	public boolean getStatus() {
		return count.get() < THRESHOLD;
	}

	@Override
	public String getErrorMessage() {
		logger.warn("*** Ping failed ***");
		return "网络异常，连接不可用";
	}

}
