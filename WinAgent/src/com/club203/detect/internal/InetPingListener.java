package com.club203.detect.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.config.GatewayReader;
import com.club203.detect.DetectService;
import com.club203.utils.NetworkUtils;

/**
 * @author hehaoxing
 * 使用InetAddress类的ping函数，需要管理员权限
 * 否则会向端口7发送请求从而导致错误的结果
 * 需要指定是否是IP层代理，以清理物理网卡推送默认路由；高速探测可以提高稳定性，但也更消耗资源
 */
public class InetPingListener implements DetectService {

	private String ipAddress;
	//超时时间，由域名解析与探测组成
	private int timeOut;
	//探测间隔
	private int interval;
	//isIpProxy指代是否是三层代理，三层代理辅助清理网关
	private boolean isIpProxy;
	
	private ScheduledExecutorService executor;
	private ScheduledExecutorService pingExecutor;
	private ScheduledFuture<?> schfuture;
	
	private volatile AtomicInteger count = new AtomicInteger(0);
	
	private static final int POOL_SIZE = 5;
	private static int THRESHOLD = 6;
	private final static Logger logger = LoggerFactory.getLogger(InetPingListener.class);
	
	public InetPingListener(String ipAddress, int timeOut, int internal) {
		super();
		this.ipAddress = ipAddress;
		this.timeOut = timeOut;
		this.interval = internal;
		this.isIpProxy = true;
	}
	
	public InetPingListener(String ipAddress, int timeOut, int internal, boolean isIpProxy) {
		super();
		this.ipAddress = ipAddress;
		this.timeOut = timeOut;
		this.interval = internal;
		this.isIpProxy = isIpProxy;
	}
	
	@Override
	public void start() {
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
		executor = Executors.newScheduledThreadPool(POOL_SIZE);
		pingExecutor = Executors.newScheduledThreadPool(POOL_SIZE);
		schfuture = executor.scheduleAtFixedRate(
			new Runnable() {
				@Override
				public void run() {
					if(count.get() > THRESHOLD) {
						cancel();
					} else if(count.get() >= THRESHOLD / 3 && count.get() % 3 == 0 && isIpProxy == true) {
						//尝试恢复线路(用于IP层代理，网关被物理网关覆盖的情况)
						logger.info("Try to recover from disconnection");
						for(String command : holdCommandList) {
							try {
								Runtime.getRuntime().exec(command).waitFor();
								logger.info("Execute: " + command);
							} catch (InterruptedException | IOException e) { 
								count.getAndIncrement();
								break;
							}
						}
					}
					Callable<Boolean> callable = new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return NetworkUtils.inetPing(ipAddress, timeOut);
						}
					};
					Future<Boolean> future = pingExecutor.submit(callable);
					try {
						boolean result = future.get(timeOut, TimeUnit.MILLISECONDS);
						if(result == true) {
							logger.info(ipAddress + " : Can be connected");
							count.set(0);
						}else {
							logger.info(ipAddress + " : Cannot be connected");
							count.incrementAndGet();
						}
					} catch (ExecutionException | TimeoutException e) {
						logger.info(ipAddress + " : Cannot be connected");
						count.incrementAndGet();
					} catch (InterruptedException e) {
						logger.info(ipAddress + " : InetPing was interrupted");
						Thread.currentThread().interrupt();
						count.set(Integer.MAX_VALUE / 2);
					}
				}
			}, 0, interval, TimeUnit.MILLISECONDS);
	}

	@Override
	public void cancel() {
		if(schfuture != null) {
			logger.info(ipAddress + " : InetPing is cancelled");
			schfuture.cancel(true);
			executor.shutdown();
			pingExecutor.shutdown();
		}
		count.set(Integer.MAX_VALUE / 2);
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
