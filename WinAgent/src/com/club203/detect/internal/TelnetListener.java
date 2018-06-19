package com.club203.detect.internal;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.detect.DetectService;

/**
 * @author hehaoxing
 * 借助Telnet检测端口的联通性
 * 检测TCP端口，不用于UDP端口检测
 */
public class TelnetListener implements DetectService {
	
	private String ipAddress;
	private int port;
	private int timeOut;
	private int interval;
	
	private ScheduledFuture<?> schfuture;
	private ScheduledExecutorService telnetExecutor;
	
	private volatile AtomicInteger count = new AtomicInteger(0);
	private static TelnetClient client = new TelnetClient();
	
	private static int THRESHOLD = 10;
	private static int POOL_SIZE = 10;
	private final static Logger logger = LoggerFactory.getLogger(TelnetListener.class);
	
	public TelnetListener(String ipAddress, int port, int timeOut, int interval) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
		this.timeOut = timeOut;
		this.interval = interval;
	}

	@Override
	public void start() {
		count.set(0);
		client.setDefaultTimeout(timeOut);
		telnetExecutor = Executors.newScheduledThreadPool(POOL_SIZE);
		schfuture = telnetExecutor.scheduleAtFixedRate(
			new Runnable() {
				@Override
				public void run() {
					if(count.get() > THRESHOLD) {
						cancel();
					} else {
						try {
							client.connect(ipAddress, port);
							client.disconnect();
							count.set(0);
							logger.info(ipAddress + ":" + port + " : Remote server telnet success");
						} catch (IOException e) {
							count.incrementAndGet();
							logger.info(ipAddress + ":" + port + " : Remote server telnet failed.");
						}
					}
				}
			}, 0, interval, TimeUnit.MILLISECONDS);
					
	}

	@Override
	public void cancel() {
		if(schfuture != null) {
			logger.info(ipAddress + " : Telnet linstener is cancelled");
			schfuture.cancel(true);
			telnetExecutor.shutdown();
		}
		count.set(Integer.MAX_VALUE / 2);
	}

	@Override
	public boolean getStatus() {
		return count.get() < THRESHOLD;
	}

	@Override
	public String getErrorMessage() {
		logger.warn("*** Telnet failed ***");
		return "网络异常，连接不可用";
	}
}
