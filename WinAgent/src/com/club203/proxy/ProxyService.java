package com.club203.proxy;

/**
 * @author hehaoxing
 * package中的方法用于为外部调用提供方法
 * 	   一种配置方式的实现，可能需要多种代理的组合
 * 	 若kill可重入，则可以简化逻辑
 */
public interface ProxyService {
	
	/**
	 * 隧道/代理启动
	 * @return 返回0即启动成功，否则为错误码  
	 * @throws
	 */
	public int startUp() throws Exception;
	
	/**
	 * 关闭隧道/代理
	 * @return 同理，返回0为成功
	 * @throws
	 */
	public int kill() throws Exception;

}
