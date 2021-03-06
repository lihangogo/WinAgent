package com.mybatis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Reader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.CheckOnlineStatus;

public class DBTools {
	//主备服务器的IP地址及其对应的连接配置名
	private static String firstIP="10.108.101.237";
	private static String secondIP="10.108.100.148";
	private static String firstEnvironment="dybatis";
	private static String secondEnvironment="second_batis";
	private static int port=33456;
	
	public static SqlSessionFactory sessionFactory;
    static{
        try {
            //使用MyBatis提供的Resources类加载mybatis的配置文件
            Reader reader = Resources.getResourceAsReader("mybatis/mybatis.cfg.xml");
            
            String environment="";
            if(CheckOnlineStatus.ping2(firstIP, 3))
            	environment=firstEnvironment;
            else
            	environment=secondEnvironment;
            
            //构建sqlSession的工厂
            sessionFactory = new SqlSessionFactoryBuilder().build(reader,environment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 创建能执行映射文件中sql的sqlSession
     * @return
     */
    public static SqlSession getSession(){
        return sessionFactory.openSession();   
    }
    
    /**
     * 关闭SqlSession
     * @param session
     */
    public static void closeSqlSession(SqlSession session){
		if(session != null){
			session.close();
		}
	}
    
    /**
	 * 获取当前的系统时间戳
	 * @return 毫秒值
	 */
	public static long getNetworkTime() {
		Socket socket=null;
		DataOutputStream dos=null;
		DataInputStream dis=null;
		byte[] bytes = null;
		int length = 0;
		
		String ip="";
		if(CheckOnlineStatus.ping2(firstIP, 3))
			ip=firstIP;
		else
			ip=secondIP;
		
		long currentTime=0;
		try {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port), 10 * 1000);
				dos = new DataOutputStream(socket.getOutputStream());
				dos.write("currentTime".getBytes());
				dos.flush();
				
				dis = new DataInputStream(socket.getInputStream());
				bytes = new byte[1024];
				length = dis.read(bytes);
				currentTime=Long.valueOf(new String(bytes,0,length));
				
				return currentTime;
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				if(dos!=null)
					dos.close();
				if(socket!=null)
					socket.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return currentTime;
	}
    
    /**
	 * 获取某个地址开头的ip地址
	 * @param ipHead
	 * @return
	 */
	public static String getIpAddressByHead(String ipHead) {
		String ip = null;
		try {
			Enumeration<NetworkInterface> allNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
			NetworkInterface networkInterface = null;
			while (allNetworkInterfaces.hasMoreElements()) {
				networkInterface = allNetworkInterfaces.nextElement();
				Enumeration<InetAddress> allInetAddress = networkInterface.getInetAddresses();
				InetAddress ipAddress = null;
				while (allInetAddress.hasMoreElements()) {
					ipAddress = allInetAddress.nextElement();
					if (ipAddress != null && ipAddress instanceof Inet4Address) {
						ip = ipAddress.getHostAddress();
						if (ip.startsWith(ipHead))
							return ip;
					}
				}
			}

		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ip;
	}
 
}
