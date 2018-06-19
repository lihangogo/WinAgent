package com.club203.service;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingWorker;
import com.club203.beans.Proxy;

/**
 * 所有服务的基类：
 * Service的子类完成了建连、中断与重连的操作：
 * 		1. 建连过程中点击关闭，建连结束后立刻撤销
 * 		2. 发生重连，当前重连操作结束后立刻中断
 * 
 * 每个实现类对应了使用了一系列流程以实现业务
 */

public abstract class Service extends SwingWorker<Boolean, Void>{
	protected Proxy proxy;
	
	protected static volatile AtomicBoolean isStart = new AtomicBoolean(false);
	protected static volatile AtomicBoolean isStop = new AtomicBoolean(false);
	protected static volatile AtomicBoolean isReconnect = new AtomicBoolean(false);
	
	public Service(Proxy proxy) {
		this.proxy = proxy;
	}
	
	/**
	 * 后台执行的任务，执行成功返回true，执行失败返回false
	 */
	@Override
	protected abstract Boolean doInBackground() throws Exception; 
	
	/**
	 * 后台执行的任务完成更新UI
	 */
	@Override
	protected abstract void done();
}
