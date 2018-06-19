package com.club203.proxy.socks5;

import com.club203.exception.socks5.ShadowsocksException;
import com.club203.proxy.ProxyService;
import com.club203.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cc.litstar.proxy.SocksServer;
import cc.litstar.server.RemoteServer;

import java.lang.reflect.Type;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hehaoxing
 * Socks5-shadowsocks客户端
 * 设定使用的本地端口为1080
 * RestAPI将用户名转换为对应的端口
 */
public class ShadowsocksProxy implements ProxyService {
	//代理服务器IP与端口
	private String serverIP;
	private int port;
	//用户鉴权信息
	private String username;
	private String passwd;
	//通过用户名获取相关信息
	private String restURL;
	//执行线程
	private Thread executor;

	//是否仅启动一次代理
	private static boolean isStartUp = false;
	private final static Logger logger = LoggerFactory.getLogger(ShadowsocksProxy.class);
	
	public ShadowsocksProxy(String serverIP, String username, String passwd, String restURL) {
		super();
		this.serverIP = serverIP;
		this.username = username;
		this.passwd = passwd;
		this.restURL = restURL;
	}

	@Override
	public synchronized int startUp() throws ShadowsocksException {
		int status = execStartup();
		if(status != 0) {
			isStartUp = false;
			throw new ShadowsocksException(status);
		}
		return status;
	}
	
	public synchronized int execStartup() {
		Callable<Integer> task = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				if(isStartUp == true) {
					logger.warn("Shadowsocks: REPEAT_STARTUP");
					return 1;
				}
				isStartUp = true;
				
				//检测本地端口是否被占用
				if(NetworkUtils.isLoclePortUsing(1080)) {
					logger.warn("Shadowsocks: LOCAL_PORT_IN_USE");
					return 2;
				}
				
				ClientResource client = new ClientResource(getRestletContext(), restURL);
				client.setRetryAttempts(0);
				
				Form form = new Form();
				form.add("username", username);
				form.add("server", serverIP);
				String data;
				try {
					data = client.post(form.getWebRepresentation()).getText();	
					if(data == null) {
						logger.warn("Shadowsocks: NO_USERINFO_FOUND");
			            return 3;
					}
				} catch (Exception e) {
					logger.warn("Shadowsocks: POST_FAIL");
					return 4;
				} finally {
					client.release();
				}

				RestResponse response = null;
				Gson gson = new Gson();  
				Type type = new TypeToken<RestResponse>(){}.getType();
				try {
					response = gson.fromJson(data, type);
				}catch (Exception e) {
					logger.warn("Shadowsocks: WRONG_RESPONSE_FORMAT");
					return 5;
				}
				
				//检测远端TCP端口是否可用
				port = response.getPort();
				if(NetworkUtils.isPortUsing(serverIP, port) == false) {
					logger.warn("Shadowsocks: REMOTE_PORT_UNUSABLE");
					return 6;
				}
				
				//启动客户端，立即返回
				RemoteServer remoteServer = new RemoteServer(serverIP, port,
															username, passwd, response.getMethod());
				SocksServer server = SocksServer.newInstance(remoteServer);
				server.start();
				logger.info("Socks5 to shadowsocks proxy is started up sucessful");
				return 0;
			}
		};
		FutureTask<Integer> futureTask = new FutureTask<Integer>(task);
		executor = new Thread(futureTask);
		executor.start();
		int status = 0;
		try {
			status = futureTask.get();
		} catch (ExecutionException e) {
			status = 11;
			logger.warn("Shadowsocks: EXECTHREAD_FAIL");
		} catch (InterruptedException e) {
			status = 12;
			logger.warn("Shadowsocks: INTERRUPTED");
		}
		return status;
	}

	@Override
	public int kill() {
		if(SocksServer.getInstance() != null) {
			SocksServer.getInstance().stop();
		}
		isStartUp = false;
		logger.info("Socks5 to shadowsocks proxy is closed sucessful");
		return 0;
	}
	
	/**
	 * 通过Restlet Context设置post的超时时间
	 */
	public Context getRestletContext() {
		Context context = new Context();
		context.getParameters().add("idleTimeout", "2500");
		context.getParameters().add("stopIdleTimeout", "2500");
		context.getParameters().add("socketTimeout", "2500");
		context.getParameters().add("socketConnectTimeoutMs", "2500");
		return context;
	}
	
	public int getPort() {
		return port;
	}

	/**
	 * Usage
	 */
	public static void main(String[] args) throws Exception {
		ShadowsocksProxy proxy = new ShadowsocksProxy("10.108.100.72", "sdhhx", "123456789",
														"http://127.0.0.1:8112/shadowsocks");
		System.out.println(proxy.startUp());
	}
	
	/**
	 * RestAPI返回的数据包
	 */
	class RestResponse {
		private String username;
		private int port;
		private String method;

		public RestResponse(String username, int port, String method) {
			super();
			this.username = username;
			this.port = port;
			this.method = method;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		@Override
		public String toString() {
			Gson gson = new Gson();
			return gson.toJson(this);
		}
	}
}
