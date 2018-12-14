package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class Server implements Runnable {
	public static void main(String[] args) {
		try {
			final ServerSocket server = new ServerSocket(33456);
			Thread th = new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {					
							Socket socket = server.accept();
							handleRequest(socket);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			th.run(); //线程运行
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void run() {
	}
	
	/**
	 * 处理请求
	 * @param socket
	 */
	public static void handleRequest(Socket socket) {

		ArrayList<ConfFileBean> list = readXMLConf();
		int len = list.size();
		byte[] inputByte = null;
		int length = 0;
		String message=null;
		DataInputStream dis = null;
		DataOutputStream dos = null;
		FileInputStream fis = null;
		BufferedWriter bw=null;
		BufferedReader br=null;
		File file=null;
		try {
			try {
				dis = new DataInputStream(socket.getInputStream());
				inputByte = new byte[1024];
				System.out.println("start input");
				length = dis.read(inputByte);
				message = new String(inputByte, 0, length);
				System.out.println(message);
				System.out.println("finish input");
				int i = 0;
				for (; i < len; i++) {
					if (list.get(i).getConfFileName().equals(message))
						break;
				}
				//获取当前时间
				if(message.equals("currentTime")) {
					String currentTime=Long.toString(System.currentTimeMillis());
					inputByte=currentTime.getBytes();
					dos = new DataOutputStream(socket.getOutputStream());
					dos.write(inputByte, 0, inputByte.length);
					dos.flush();
				}
				// 获取内网IP地址
				else if(message.equals("InnerNetIP")) {
					inputByte = "ok".getBytes();
					dos = new DataOutputStream(socket.getOutputStream());
					dos.write(inputByte, 0, inputByte.length);
					dos.flush();
					
					inputByte = new byte[1024];
					length = dis.read(inputByte);
					message = new String(inputByte, 0, length);
					File f=new File("conf/ip.txt");
					
					br=new BufferedReader(new FileReader(f));
					ArrayList<String> ipList=new ArrayList<String>();
					String temp=null;
					while((temp=br.readLine())!=null)
						ipList.add(temp.trim());
					br.close();
					
					if(!ipList.contains(message)) {
						bw=new BufferedWriter(new FileWriter(f,true));
						bw.write(message);
						bw.newLine();
						bw.flush();
						bw.close();	
					}
				}
				//获取解密算法类型
				else if(message.equals("deAlg")) {
					String data=null;
					for(ConfFileBean cfb:list) {
						if(cfb.getConfFileName().equals(message))
							data=cfb.getConfFilePath();
					}
					System.out.println(data);
					inputByte = data.getBytes();
					dos = new DataOutputStream(socket.getOutputStream());
					dos.write(inputByte, 0, inputByte.length);
					dos.flush();
				}else {
					inputByte = list.get(i).getConfFileVersion().getBytes();
					dos = new DataOutputStream(socket.getOutputStream());
					dos.write(inputByte, 0, inputByte.length);
					dos.flush();
					
					inputByte = new byte[1024];
					length = dis.read(inputByte);
					message = new String(inputByte, 0, length);
					if(message.equals("get")) {
						file=new File(list.get(i).getConfFilePath());
						fis=new FileInputStream(file);
						inputByte = new byte[1024];
						while((length=fis.read(inputByte,0,inputByte.length))>0) {
							dos.write(inputByte,0,length);
							dos.flush();
						}
					}
				}
				
			} finally {
				if(bw!=null)
					bw.close();
				if (dis != null)
					dis.close();
				if (dos != null)
					dos.close();
				if (fis != null)
					fis.close();
				if (socket != null)
					socket.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析XML文件
	 * @return
	 */
	public static ArrayList<ConfFileBean> readXMLConf() {
		ArrayList<ConfFileBean> confList = null;
		File f = null;
		ConfFileBean confFileBean = null;
		try {
			confList = new ArrayList<ConfFileBean>();
			f = new File("conf/Version.xml");
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

}
