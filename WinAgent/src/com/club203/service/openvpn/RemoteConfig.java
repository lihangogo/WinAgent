package com.club203.service.openvpn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.club203.beans.ConfFileBean;
import com.club203.beans.Configuration;

/**
 * 负责远程配置管理
 * @author 李瀚
 *
 */
public class RemoteConfig {
	private final static Logger logger = LoggerFactory.getLogger(RemoteConfig.class);
	
	private int length = 0;

	private byte[] sendBytes = null;
	private byte[] bytes = null;

	private Socket socket = null;

	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	private FileOutputStream fos = null;
	private FileInputStream fis = null;
	private File file = null;
	private ArrayList<ConfFileBean> list = null;
	private String ip="10.108.101.237";
	private int port=33456;
	private static String versionConfPath="conf/Version.xml";
	private static String systemConfPath="conf/SystemConf.xml";
	
	/**
	 * 构造函数
	 */
	public RemoteConfig() {
		list=new ArrayList<ConfFileBean>();
		
	}
	
	/**
	 * 检查配置
	 * @return 是否更新配置成功
	 */
	public boolean checkConfig() {
		opt();
		return true;
	}

	/**
	 * 具体操作步骤
	 */
	private void opt() {
		list = readXMLConf();
		String confName=null;
		for (int i = 0; i < list.size(); i++) {
			confName=list.get(i).getConfFileName();
			if (confName.startsWith("O")) {
				innerIPComm();	
			}else if(confName.startsWith("T")) {
				encryComm();
			}else {
				doubleComm(list.get(i));
			}
		}
	}
	
	/**
	 * 用于加解密算法的更新
	 */
	private void encryComm() {
		try {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port), 10 * 1000);
				dos = new DataOutputStream(socket.getOutputStream());
				dos.write("deAlg".getBytes());
				dos.flush();
				dis = new DataInputStream(socket.getInputStream());
				bytes = new byte[1024];
				length = dis.read(bytes);
				updateXMLConf("TdeAlgPath", new String(bytes, 0, length));
				logger.info("update XML success");
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
	}
	
	/**
	 * 用于上传内网IP地址
	 */
	private void innerIPComm() {
		String head="10.";
		String innerNetIP=getInnerNetIp(head);
		try {
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port), 10 * 1000);
				dos = new DataOutputStream(socket.getOutputStream());
				dos.write("InnerNetIP".getBytes());
				dos.flush();	
				dis = new DataInputStream(socket.getInputStream());
				bytes = new byte[1024];
				length = dis.read(bytes);
				String message = new String(bytes, 0, length);
				if(message.equals("ok")) {
					dos.write(innerNetIP.getBytes());
					dos.flush();
				}	
				logger.info("upload inner ip success");
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
	}	
	
	/**
	 * 两次通信（询问加应答算一次通信）
	 * @param cfb
	 */
	private void doubleComm(ConfFileBean cfb) {
		try {
			try {
				// 询问服务器配置文件的最新版本号
				socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port), 10 * 1000);
				dos = new DataOutputStream(socket.getOutputStream());
				dos.write(cfb.getConfFileName().getBytes());
				dos.flush();

				// 读取最新版本的版本号
				dis = new DataInputStream(socket.getInputStream());
				sendBytes = new byte[1024];
				length = dis.read(sendBytes, 0, sendBytes.length);
				String message = new String(sendBytes, 0, length);

				// 服务器上有该配置文件的新版本
				if (!message.equals(cfb.getConfFileVersion())) {
					dos.write("get".getBytes());
					dos.flush();

					String path1=cfb.getConfFilePath();
					file = new File(path1);
					
					fos = new FileOutputStream(file);
					sendBytes = new byte[1024];
					while ((length = dis.read(sendBytes, 0, sendBytes.length)) > 0) {
						fos.write(sendBytes, 0, length);
						fos.flush();
					}
					updateXMLConf(cfb.getConfFileName()+"Version", message);
				} else {
					dos.write("not-get".getBytes());
					dos.flush();
				}
				socket.close();
				logger.info("check "+cfb.getConfFileName()+" version success");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dos != null)
					dos.close();
				if (dis != null)
					dis.close();
				if (fis != null)
					fis.close();
				if (fos != null)
					fos.close();
				if (socket != null)
					socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取以10开头的IP地址
	 * 
	 * @return
	 */
	private String getInnerNetIp(String head) {
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
						ip = ipAddress.getHostAddress().trim();
						if (ip.startsWith(head))
							return ip;
					}
				}
			}

		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * 读取配置列表的XML
	 */
	private ArrayList<ConfFileBean> readXMLConf() {
		ArrayList<ConfFileBean> confList = null;
		File f = null;
		ConfFileBean confFileBean = null;
		try {
			confList = new ArrayList<ConfFileBean>();
			f = new File(versionConfPath);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(f);
			int len = document.getElementsByTagName("confItem").getLength();
			for (int i = 0; i < len; i++) {
				confFileBean = new ConfFileBean();
				String confName = document.getElementsByTagName("confItem").item(i).getFirstChild().getNodeValue();
				confFileBean.setConfFileName(confName);
				confFileBean.setConfFileVersion(
						document.getElementsByTagName(confName + "Version").item(0).getFirstChild().getNodeValue());
				confFileBean.setConfFilePath(
						document.getElementsByTagName(confName + "Path").item(0).getFirstChild().getNodeValue());
				confList.add(confFileBean);
			}
			return confList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return confList;
	}
	
	/**
	 * 读取系统配置文件的某些字符串
	 * @param name
	 * @return
	 */
	public static String getBean_2(String name) {
		File f=null;
		try {
			f = new File(systemConfPath);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(f);
			String confName = document.getElementsByTagName(name).item(0).getFirstChild().getNodeValue();
			return confName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 读取系统配置文件
	 * @return
	 */
	public static Configuration getBean() {
		Configuration configuration=new Configuration();
		File f=null;
		try {
			f = new File(systemConfPath);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(f);
			String[] ServiceType=new String[2];
			for(int i=0;i<2;i++)
				ServiceType[i]=document.getElementsByTagName("ServiceType").item(i).getFirstChild().getNodeValue();
			String[] ProxyType=new String[6];
			for(int i=0;i<6;i++)
				ProxyType[i]=document.getElementsByTagName("ProxyType").item(i).getFirstChild().getNodeValue();
			String[] SchoolServiceType=new String[2];
			SchoolServiceType[0]=document.getElementsByTagName("SchoolServiceType").item(0).getFirstChild().getNodeValue();
			HashMap<String, String> StartList=new HashMap<String,String>();
			int len = document.getElementsByTagName("StartList").getLength();
			for(int i=0;i<len;i++) 
				StartList.put(document.getElementsByTagName("key1").item(i).getFirstChild().getNodeValue(),
						document.getElementsByTagName("value1").item(i).getFirstChild().getNodeValue());
			HashMap<String, String> StopList=new HashMap<String,String>();
			len = document.getElementsByTagName("StopList").getLength();
			for(int i=0;i<len;i++) 
				StopList.put(document.getElementsByTagName("key2").item(i).getFirstChild().getNodeValue(),
						document.getElementsByTagName("value2").item(i).getFirstChild().getNodeValue());
			HashMap<String, String> ReconnectList=new HashMap<String,String>();
			len = document.getElementsByTagName("ReconnectList").getLength();
			for(int i=0;i<len;i++) 
				ReconnectList.put(document.getElementsByTagName("key3").item(i).getFirstChild().getNodeValue(),
						document.getElementsByTagName("value3").item(i).getFirstChild().getNodeValue());
			configuration.setProxyType(ProxyType);
			configuration.setReconnectList(ReconnectList);
			configuration.setSchoolServiceType(SchoolServiceType);
			configuration.setServiceType(ServiceType);
			configuration.setStartList(StartList);
			configuration.setStopList(StopList);
			return configuration;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configuration;
	}
	
	/**
	 * 获取当前设置的加密解密算法类型
	 * @return
	 */
	public static String getEncryType() {
		String str=null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(versionConfPath));
			str=document.getElementsByTagName("TdeAlgPath").item(0).getFirstChild().getNodeValue();
			return str;
		}catch(Exception e) {
			logger.info("Get encry type failed");
		}
		return str;
	}
	
	/**
	 * 更新配置文件，将版本号等写入XML
	 * @param nodeName
	 * @param newNodeValue
	 */
	private void updateXMLConf(String nodeName,String newNodeValue) {
		File f = null;
		try {
			f = new File(versionConfPath);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(f);
			NodeList list=document.getElementsByTagName(nodeName);
			list.item(0).getFirstChild().setNodeValue(newNodeValue);
			TransformerFactory tff=TransformerFactory.newInstance();
			Transformer transformer=tff.newTransformer();
			DOMSource domSource=new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			StreamResult res=new StreamResult(new FileOutputStream(versionConfPath));
			transformer.transform(domSource, res);	
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
}
