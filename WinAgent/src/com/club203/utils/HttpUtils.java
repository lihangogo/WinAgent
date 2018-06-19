package com.club203.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author hehaoxing
 * 
 * 模拟使用Http代理
 * 规避发生在本地的域名解析，域名解析应该发生在远端
 */
public class HttpUtils {
	
	/**
	 * 使用HTTP代理访问域名，返回HTTP状态码
	 * 
	 * @param proxyHost Http代理服务器IP
	 * @param proxyPort Http代理服务器端口
	 * @param domain    请求域名
	 * @param isHttps   Http或者Https
	 * @throws IOException 
	 */
	public static int accessWithHttpProxy(String proxyHost, int proxyPort, String domain, boolean isHttps) throws Exception {
		InetSocketAddress proxyAddr = new InetSocketAddress(proxyHost, proxyPort);
        Socket underlying = new Socket(new Proxy(Proxy.Type.HTTP, proxyAddr));
        //读线程超时时间
        underlying.setSoTimeout(1000);
        Socket socket = null;
        if(isHttps == true) {
        	underlying.connect(InetSocketAddress.createUnresolved(domain, 443), 1500);
        	socket = (SSLSocket) getSSLSocketFactory().createSocket(
                    underlying,
                    proxyHost,
                    proxyPort,
                    true); 
        } else {
        	socket = underlying;
        	socket.connect(InetSocketAddress.createUnresolved(domain, 80), 1500);
        }
        
        String data = httpGetText(domain, "/");
        
        OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream());          
        BufferedWriter bufferedWriter = new BufferedWriter(streamWriter);
        bufferedWriter.write(data);
        bufferedWriter.flush();
        
        BufferedInputStream streamReader = new BufferedInputStream(socket.getInputStream());  
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(streamReader, "utf-8"));
        String line = null;
        int code = -1;
        while((line = bufferedReader.readLine())!= null) {
        	if(line.contains("HTTP/1.1")) {
        		String[] lineSpt = line.split("\\s+");
        		if(line.length() >= 2) {
        			code = Integer.parseInt(lineSpt[1]);
        			//System.out.println(code);
        		}
        		break;
        	}
        }
        bufferedReader.close();
        bufferedWriter.close();
        socket.close();
        return code;
	}
	
	/**
	 * 获取SSLSocketFactory，忽略所有的SSL验证
	 */
	public static SSLSocketFactory getSSLSocketFactory() throws Exception {
		SSLContext context = SSLContext.getInstance("SSL"); 
		// 初始化 
		TrustManager[] tm = { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
		} };
		context.init(null, tm, new SecureRandom()); 
		SSLSocketFactory factory = context.getSocketFactory();	
		return factory;
	}
	
	/**
	 * 模拟浏览器HTTP GET请求报文
	 * @param host 
	 * @param path 
	 */
	public static String httpGetText(String host, String path) {
        //构造数据报文
        StringBuilder tdata = new StringBuilder("");
        tdata.append("GET " + path + " HTTP/1.1\r\n");
        tdata.append("Accept: */*\r\n");
        tdata.append("Accept-Encoding: gzip, deflate, br\r\n");
        tdata.append("Accept-Language: zh-CN,zh;q=0.9\r\n");
        tdata.append("Host: " + host + "\r\n");
        tdata.append("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)" +
        			" Chrome/62.0.3202.94 Safari/537.36\r\n");
        tdata.append("\r\n");
        String data = new String(tdata);
        return data;
	}

}
