package com.club203.detect.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.config.GatewayReader;
import com.club203.detect.DetectService;

/**
 * 清理默认路由表的功能(多网卡)
 * 阻止默认网段推送，暂时废弃
 * @author hehaoxing
 */
public class NetworkHold implements DetectService {
	
	private ScheduledExecutorService executor;
	private ScheduledFuture<?> schfuture;
	private static final int POOL_SIZE = 2;
	
	private final static Logger logger = LoggerFactory.getLogger(NetworkHold.class);
	
	public NetworkHold() {
		super();
	}
	
	@Override
	public void start() {
		logger.info("Starting keeping connection alive");
		executor = Executors.newScheduledThreadPool(POOL_SIZE);
		schfuture = executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				//多网卡环境下定期清理网关
				String defaultGateway = GatewayReader.getDefaultGateway();
				List<String> anotherGateway = GatewayReader.getAnotherGateway();
				List<String> holdCommandList = new ArrayList<>();
				holdCommandList.add("route delete 0.0.0.0 mask 0.0.0.0 " + defaultGateway);
				for(String gateway : anotherGateway) {
					holdCommandList.add("route delete 0.0.0.0 mask 0.0.0.0 " + gateway);
				}
				for(String command : holdCommandList) {
					try {
						Runtime.getRuntime().exec(command).waitFor();
						logger.info("Execute: " + command);
					} catch (InterruptedException e) { 
						break;
					} catch (IOException e) { }
				}
			}
		}, 30, 15, TimeUnit.MINUTES);
	}

	@Override
	public void cancel() {
		try {
			schfuture.cancel(true);
			Runtime.getRuntime().exec("route add 0.0.0.0 mask 0.0.0.0 " + GatewayReader.getDefaultGateway()).waitFor();
			logger.info("Keeping connection alive is cancelled");
		} catch (InterruptedException | IOException e) { }
		finally {
			executor.shutdown();
		}
	}

	@Override
	public boolean getStatus() {
		return true;
	}

	@Override
	public String getErrorMessage() {
		//不存在超过阈值退出的情况
		return "";
	}
}
