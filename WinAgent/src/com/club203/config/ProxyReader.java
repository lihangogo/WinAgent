package com.club203.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.dialog.MessageDialog;
import com.club203.utils.EncryptUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 从文件中读取代理信息
 * 
 * @author lihan
 */
public class ProxyReader {
	private String confPath = "conf/ProxyList.conf";
	
	private final static Logger logger = LoggerFactory.getLogger(ProxyReader.class);

	private static Map<String, Proxy> proxyList = null;

	public static Map<String, Proxy> getProxy() {
		if (proxyList == null) {
			new ProxyReader().readProxy();
		}
		return proxyList;
	}

	private ProxyReader() {
		super();
	}

	private void readProxy() {	
		// 读取Json字符串
		String json = "";
		String line = null;
		logger.info("Starting reading proxy information from ProxyList.conf");
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(confPath), "UTF-8"))) {
			while ((line = in.readLine()) != null) {
				// 注释以;开头
				if (!line.startsWith(";")) {
					json += line;
				}
			}
		} catch (FileNotFoundException e) {
			logger.error("Cannot find ProxyList.conf");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Cannot read ProxyList.conf");
			e.printStackTrace();
		} finally {
			
		}
		json = json.replaceAll("\\s+", "");
		// 将字符串读入配置文件
		proxyList = new LinkedHashMap<>();
		Gson gson = new Gson();
		List<Proxy> tmpProxyList = new ArrayList<>();
		Type type = new TypeToken<ArrayList<Proxy>>() {
		}.getType();
		try {
			// 过滤非法字符
			tmpProxyList = gson.fromJson(json, type);
		} catch (Exception e) {
			new MessageDialog("不能读取配置文件，请检查配置文件格式是否正确").show();
			logger.error("Cannot read configure file -- errors found in the file");
			e.printStackTrace();
			System.exit(0);
		}
		
		for (Proxy proxy : tmpProxyList) {
			List<String> list=proxy.getServerIP();
			ArrayList<String> newList=new ArrayList<String>();
			for(String str:list) {
				if(!str.contains(".")) 
					newList.add(EncryptUtils.decrypt1(str));			
			}
			proxy.setServerIP(newList);
			if (proxy.proxyCheck() == true) {
				proxyList.put(proxy.getProxyName(), proxy);
				logger.info(proxy.toString());
			} else {
				logger.warn("Error checked in proxy:" + proxy.toString());
			}
		}
		logger.info("Reading ProxyList.conf finished");
	}

	public static void main(String[] args) {
		ProxyReader.getProxy();
	}

}
