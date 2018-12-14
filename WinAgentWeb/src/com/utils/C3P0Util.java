package com.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * C3P0连接池
 * @author lihan
 */
public class C3P0Util {

	public static ComboPooledDataSource comboPooleDataSource = new ComboPooledDataSource("MyDriver");
	
	/**
	 * 获取连接对象
	 * 
	 * @return 返回数据库连接对象
	 */
	public static Connection getConnection(){
		
		try {
			return comboPooleDataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * 获取数据源对象
	 * @return
	 */
	public static DataSource getDataSource(){
		return comboPooleDataSource;
	}
	
	/**
	 * 释放连接
	 * @param conn 数据库连接对象
	 * @param stmt Statement对象
	 * @param rs Result对象
	 */
	public static void release(Connection conn, Statement stmt, ResultSet rs) {

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e){
				e.printStackTrace();
			}
			conn = null;
		}

		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt = null;
		}

		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rs = null;
		}
	}
	
}
