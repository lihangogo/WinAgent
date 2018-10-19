package com.club203.proxy.openvpn;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.config.ConfReader;
import com.club203.exception.openvpn.OpenVPNException;
import com.club203.proxy.ProxyService;
import com.club203.utils.EncryptUtils;

/**
 * 启动OpenVPN客户端： 仅启用一个OpenVPN客户端，启动过多的客户端反而会造成虚拟网卡错误。 OpenVPN的所有配置文件均被加密，见util类。
 * 
 * 使用Ping检测客户端是否可用，服务器随主机启动 UDP模拟消息验证VPN是否可用也是可行的
 */
public class Openvpn implements ProxyService {
	// OpenVPN执行线程
	private Process process;
	// OpenVPN对应的文件名
	private String filename;
	// OpenVPN启动执行线程
	private Thread executor = null;
	// OpenVPN读取执行线程
	private Thread readExecutor = null;
	// 读取线程Future
	private FutureTask<Integer> getStatusFuture = null;

	// 是否仅启动一个OpenVPN客户端
	private static boolean isStartUp = false;
	// OpenVPN配置文件目录
	private static final String path = "./conf/openvpn/";
	// OpenVPN配置文件解密位置
	private static final String deConfigPath = "./conf/openvpn/openvpn.ovpn";
	// OpenVPN CA文件位置
	private static final String caFilePath = "./conf/openvpn/openvpn-ca/ca.crt";

	// OpenVPN key文件位置
	private static final String keyFilePath = "./conf/openvpn/openvpn-ca/client.key";
	// OpenVPN crt文件位置
	private static final String crtFilePath = "./conf/openvpn/openvpn-ca/client.crt";

	// OpenVPN鉴权文件位置(鉴权对话框生成，SwingWorker验证，OpenVPN客户端读取)
	private static final String authFilePath = "./passwd";
	// OpenVPN读取线程超时时间
	private static final int timeout = 60;
	// 日志生成
	private final static Logger logger = LoggerFactory.getLogger(Openvpn.class);

	public Openvpn(String filename) {
		this.filename = filename;
	}

	/**
	 * 程序启动后，初始化代理
	 */
	public static int init() {
		// 启动前关闭所有OpenVPN客户端，正常情况下一台PC仅启动一个Windows网卡
		killall();
		// 清理旧配置文件
		cleanupFiles();
		return 0;
	}

	/**
	 * 启动OpenVPN隧道 删除鉴权文件会导致自动重连失效
	 */
	public synchronized int startUp() throws OpenVPNException {
		int status = execStartup();
		if (status != 0) {
			isStartUp = false;
			throw new OpenVPNException(status);
		}
		return status;
	}

	/**
	 * 启动OpenVPN客户端
	 */
	public synchronized int execStartup() {
		Callable<Integer> task = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				int errorStatus = 0;
				if (isStartUp == true) {
					errorStatus = 1;
					logger.warn("OpenVPN: REPEAT_STARTUP");
					return 1;
				}
				isStartUp = true;
				// 配置文件解密
				EncryptUtils td = new EncryptUtils(ConfReader.getConfig().getEncryptInfo());
				try {
					new File(deConfigPath).delete();
					new File(caFilePath).delete();
					td.decrypt(path + Openvpn.this.filename, deConfigPath);
					td.decrypt(path + "openvpn-ca/ca", caFilePath);
					
					td.decrypt(path + "openvpn-ca/client_en.key", keyFilePath);
					td.decrypt(path + "openvpn-ca/client_en.crt", crtFilePath);
					
				} catch (Exception e) {
					logger.warn(e.toString());
					logger.warn("OpenVPN: CONFIG_DECRYPT_FAIL");
					return 10;
				}
				// 打开新的OPENVPN连接
				try {
					process = Runtime.getRuntime().exec("openvpn --config " + 
								path + "openvpn.ovpn");
					logger.warn("Creating openvpn client process");
				} catch (Exception e) {
					errorStatus = 2;
					logger.warn("OpenVPN: CANNOT_EXEC");
					return 2;
				}
				// 读取线程执行结果
				Callable<Integer> getStatusTask = new OpenvpnProcessReader(process);
				getStatusFuture = new FutureTask<>(getStatusTask);
				readExecutor = new Thread(getStatusFuture);
				readExecutor.start();
				try {
					errorStatus = getStatusFuture.get(timeout, TimeUnit.SECONDS);
				} catch (ExecutionException e) {
					errorStatus = 11;
					logger.warn("OpenVPN: READTHREAD_FAIL");
				} catch (TimeoutException e) {
					errorStatus = 12;
					logger.warn("OpenVPN: OPENVPN_TIMEOUT");
				} catch (InterruptedException e) {
					errorStatus = 15;
					logger.warn("OpenVPN: INTERRUPTED");
				}
				// 启动成功
				if (errorStatus == 0) {
					new File(path + "openvpn").delete();
					logger.info("Openvpn client process is started up sucessful");
					return 0;
				}
				return errorStatus;
			}

		};
		// 隧道建立
		FutureTask<Integer> futureTask = new FutureTask<Integer>(task);
		executor = new Thread(futureTask);
		executor.start();
		int status = 0;
		try {
			status = futureTask.get();
		} catch (ExecutionException e) {
			status = 13;
			logger.warn("OpenVPN: EXECTHREAD_FAIL");
		} catch (InterruptedException e) {
			status = 15;
			logger.warn("OpenVPN: INTERRUPTED");
		}
		return status;
	}

	/**
	 * 关闭OpenVPN客户端，操作发生在建连之后
	 */
	public synchronized int kill() {
		if (process != null) {
			process.destroy();
			getStatusFuture.cancel(true);
			logger.info("Destroying openvpn client process sucessful");
		}
		isStartUp = false;
		new File(path + "openvpn.ovpn").delete();
		new File(path + "openvpn-ca/ca.crt").delete();
		logger.info("Openvpn is closed sucessful");
		return 0;
	}

	/**
	 * 中断OpenVPN建连操作，操作发生在建连过程中
	 */
	public synchronized void interrupt() {
		if (executor != null) {
			executor.interrupt();
		}
		if (readExecutor != null) {
			readExecutor.interrupt();
			process.destroy();
		}
	}

	/**
	 * 代理退出后，清理资源
	 */
	public static int clean() throws Exception {
		// 关闭所有OpenVPN客户端
		Openvpn.killall();
		// 解密的配置文件有可能还存在
		Openvpn.cleanupFiles();
		// 删掉过去输入的用户名密码文件
		new File(authFilePath).delete();
		return 0;
	}

	/**
	 * 清理所有OpenVPN进程
	 */
	public static int killall() {
		try {
			String command = "taskkill /F /IM openvpn.exe  /T";
			Runtime.getRuntime().exec(command).waitFor();
			logger.info("Destroying all openvpn client process sucessful");
			return 0;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		}
	}

	/**
	 * 清理解密后的文件
	 */
	public static void cleanupFiles() {
		new File(deConfigPath).delete();
		new File(caFilePath).delete();
		
		new File(keyFilePath).delete();
		new File(crtFilePath).delete();
		
	}

	/**
	 * 获取鉴权文件文件名
	 */
	public static String getAuthenFilepath() {
		return authFilePath;
	}

	@Override
	public String toString() {
		return "OpenVPN [process=" + process + ", filename=" + filename + ", isStartUp=" + isStartUp + "]";
	}

	/**
	 * 获取OpenVPN进程的运行状态
	 */
	class OpenvpnProcessReader implements Callable<Integer> {

		private Process process;

		public OpenvpnProcessReader(Process process) {
			this.process = process;
		}

		public Integer call() throws InterruptedException {
			if (process == null) {
				logger.warn("OpenVPN: PROCESS_START_FAIL");
				return 3;
			}
			try (BufferedReader in = new BufferedReader(
					new InputStreamReader(process.getInputStream(), Charset.forName("GBK")))) {
				logger.info("Starting reading openvpn status from process");
				String line = null;
				// readLine()方法不抛出中断异常，但关闭进程会因读到null，从而跳出响应中断
				while ((line = in.readLine()) != null) {
					// System.out.println(line);
					if (line.contains("Initialization Sequence Completed")) {
						return 0;
					} else if (line.contains("AUTH_FAILED")) {
						// AUTH_FAIL
						logger.warn("OpenVPN: AUTH_FAIL");
						return 4;
					} else if (line.contains("Error opening configuration file")) {
						// CONFIG_FAIL
						logger.warn("OpenVPN: CONFIG_FAIL");
						return 5;
					} else if (line.contains("No such file or directory")) {
						// CONFIG_NOTFOUND
						logger.warn("OpenVPN: CONFIG_NOTFOUND");
						return 6;
					} else if (line.contains("Exiting due to fatal error")) {
						// TAPUSED_OR_CERTERROR
						logger.warn("OpenVPN: TAPUSED_OR_CERTERROR");
						return 7;
					}
					// 网络不连通时，与配置文件中其他服务器建连
					else if (line.contains("TLS Error: TLS key negotiation failed to occur within")
							&& line.contains("check your network connectivity")) {
						line = in.readLine();
						if (line.contains("TLS handshake failed")) {
						}
					} else if (line.contains("TLS handshake failed")) {
						// HANDSHAKE_FAIL
						logger.warn("OpenVPN: HANDSHAKE_FAIL");
						return 8;
					}
				}
			} catch (IOException e) {
				// READTHREAD_IO_FAIL
				logger.info("OpenVPN: READTHREAD_IO_FAIL");
				return 9;
			}
			if (Thread.currentThread().isInterrupted()) {
				// INTERRUPTED
				logger.info("OpenVPN: INTERRUPTED");
				return 15;
			}
			// UNKNOWN_ERROR
			logger.info("OpenVPN: UNKNOWN_ERROR");
			return 20;
		}
	}
}
