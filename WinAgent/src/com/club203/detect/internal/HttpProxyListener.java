package com.club203.detect.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.detect.DetectService;
import com.club203.proxy.http.HttpProxy;

/**
 * @author hehaoxing
 * 检测HTTP代理的可用性，需要指定域名
 * 模拟了一个Get请求，由服务器端对域名进行解析
 */
public class HttpProxyListener implements DetectService {
	
	private String serverIP;
	private int port;
	private String domain;
	
	private Thread executor; 
	private volatile AtomicInteger count = new AtomicInteger(0);
	
	private static final int THRESHOLD = 12;
	private final static Logger logger = LoggerFactory.getLogger(HttpProxyListener.class);

	public HttpProxyListener(String serverIP, int port, String domain) {
		super();
		this.serverIP = serverIP;
		this.port = port;
		this.domain = domain;
	}

	@Override
	public void start() {
		executor = new Thread(() -> {
			try {
				new URL(domain);
			} catch (MalformedURLException e) {
				count.set(Integer.MAX_VALUE / 2);
				logger.info(domain + " : Invalid url given");
				return;
			}
			if(domain == null || domain.length() == 0) {
				count.set(Integer.MAX_VALUE / 2);
				logger.info(domain + " : Empty url given");
				return;
			}
			if(domain.startsWith("ftp://")) {
				count.set(Integer.MAX_VALUE / 2);
				logger.info(domain + " : Ftp url given");
				return;
			}
			if(!(domain.startsWith("https://") || domain.startsWith("http://"))) {
				domain = "https://" + domain;
			}
			//启动线程监听任务
			while(count.get() < THRESHOLD && !Thread.currentThread().isInterrupted()) {
				try {
					//模拟HTTP代理访问
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					count.set(Integer.MAX_VALUE / 2);
					return;
				}
				if(HttpProxy.isHttpProxyAvailable(serverIP, port, domain) == true) {
					count.set(0);
					logger.info(domain + " : Http proxy is usable");
				} else {
					count.incrementAndGet();
					logger.info(domain + " : Http proxy is not usable");
				}
			}	
		});
		executor.start();
	}
  
	@Override
	public synchronized void cancel() {
		logger.info("Http proxy detection is canceled");
		count.set(Integer.MAX_VALUE / 2);
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
		logger.warn("*** Http proxy access failed ***");
		return "HTTP代理异常，代理服务不可用";
	}
	
}
