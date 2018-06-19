package com.club203.detect;

public interface DetectService {
	/**
	 * 启动监听服务
	 */
	public void start();
	/**
	 * 取消监听服务
	 */
	public void cancel();
	/**
	 * 获得当前监听状态，返回false表示监听到网络故障
	 */
	public boolean getStatus();
	/**
	 * 获取错误信息
	 */
	public String getErrorMessage();
}
