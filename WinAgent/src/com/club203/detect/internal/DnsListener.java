package com.club203.detect.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
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

import com.club203.detect.DetectService;

public class DnsListener implements DetectService {
	
	private String domainName;	
	//分别用于执行定时任务与实际解析任务
	private ScheduledExecutorService executor;
	private ScheduledExecutorService digExecutor;
	
	private ScheduledFuture<?> schfuture;
	private volatile AtomicInteger count = new AtomicInteger(0);
	private static final int POOL_SIZE = 5;
	private static final int THRESHOLD = 8;
	
	private final static Logger logger = LoggerFactory.getLogger(DnsListener.class);
	
	public DnsListener(String domainName) {
		this.domainName = domainName;
	}
	
	@Override
	public void start() {
		logger.info("Starting detecting dns resolution: " + domainName);
		//如果不想重新建立实例的话，就要保证可重入。
		count.set(0);
		executor = Executors.newScheduledThreadPool(POOL_SIZE);
		digExecutor = Executors.newScheduledThreadPool(POOL_SIZE);
		schfuture = executor.scheduleAtFixedRate(
			new Runnable() {
				@Override
				public void run() {
					if(count.get() > THRESHOLD) {
						cancel();
					}
					//这里应该加到线程池中，避免反复创建线程浪费资源
					Callable<String> callable = new Callable<String>() {
						@Override
						public String call() throws Exception {
							try {
								String hostip = InetAddress.getByName(domainName).getHostAddress();
								logger.info(domainName + " : The hosts IP address is " + hostip);
								return hostip;
							} catch (UnknownHostException e) {
								logger.info(domainName + " : The hosts IP address is not found");
								return null;
							}
						}
					};
					//execute只能执行Runnable，submit实现中调用了execute
					Future<String> future = digExecutor.submit(callable);
					try {
						String hostAdress = future.get(5, TimeUnit.SECONDS);
						if(hostAdress != null) {
							count.set(0);
						} else {
							count.incrementAndGet();
						}
					} catch (ExecutionException | TimeoutException e) {
						logger.info(domainName + " : DNS resolution times out");
						count.incrementAndGet();
					} catch (InterruptedException e) {
						logger.info(domainName + " : DNS resolution was interrupted");
						Thread.currentThread().interrupt();
						count.set(Integer.MAX_VALUE / 2);
					}
					
				}
			}, 0, 5, TimeUnit.SECONDS);
	}
	
	@Override
	public void cancel() {
		if(schfuture != null) {
			logger.info(domainName + " : DNS resolution is cancelled");
			schfuture.cancel(true);
			//通过将线程池状态设置为SHUTDOWN，其他任务提交抛出rejectedExecution以阻止执行，但不会关闭已经提交的任务
			executor.shutdown();
			digExecutor.shutdown();
		}
		count.set(Integer.MAX_VALUE / 2);
	}
	
	public synchronized boolean cancelIfCantDig() {
		if(count.get() > THRESHOLD && schfuture != null) {
			schfuture.cancel(true);
			executor.shutdown();
			digExecutor.shutdown();
			count.set(Integer.MAX_VALUE / 2);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean getStatus() {
		return count.get() < THRESHOLD;
	}

	@Override
	public String getErrorMessage() {
		logger.warn("*** DNS resolution failed ***");
		return "DNS解析异常，连接不可用";
	}

}
