package com.club203.utils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * @author hehaoxing
 * 
 * jsch是实现了的sftp协议的文件传输协议
 * 似乎不太安全
 */
public class SftpUtils {
	/**
	 * 本机文件上传到服务器
	 * @param: ipAddress 目的服务器IP
	 * @param: username 目的服务器账户
	 * @param: password 目的服务器密码
	 * @param: srcPath 本地文件路径(请使用绝对路径)
	 * @param: dstPath 目的文件路径(请使用绝对路径) 
	 */
	public static boolean putFileToRemote(String ipAddress, String username, String password, String srcPath, String dstPath) {		
		JSch jsch = new JSch();
		try{
			//connect session
			Session session = jsch.getSession(username, ipAddress, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");  
			session.connect();
			// sftp remotely  
		    ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");  
		    channel.connect();  
		    // get ： 获取文件并存放
		    channel.put(srcPath, dstPath, ChannelSftp.OVERWRITE);  
		    // close connection
		    channel.disconnect();
		    session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
