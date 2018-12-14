package com.utils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务管理 
 * @author lihan
 */
public class TransactionManager {
	
	//解决同步问题
	private static ThreadLocal<Connection> tl = new ThreadLocal<Connection>();
	
	/**
	 * 获取连接
	 * @return Connection连接对象
	 */
	public static Connection getConnection(){
		//该方法返回当前线程所对应的线程局部变量
		Connection conn = tl.get();
		if(conn == null){
			conn = C3P0Util.getConnection();
			tl.set(conn);
		}
		return conn;
	}
	
	/**
	 * 开启事务
	 */
	public static void startTransaction(){
		try {
			Connection conn = getConnection();
			conn.setAutoCommit(false);	//不能自动提交
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 提交事务
	 */
	public static void commit(){
		try {
			Connection conn = getConnection();
			conn.commit();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 事务回滚
	 */
	public static void rollback(){
		try {
			Connection conn = getConnection();
			conn.rollback();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 释放连接
	 */
	public static void release(){
		try {
			Connection conn = getConnection();
			conn.close();
			tl.remove();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
