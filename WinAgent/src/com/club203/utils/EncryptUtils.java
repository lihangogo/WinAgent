package com.club203.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;

import com.club203.service.openvpn.RemoteConfig;
/**
 * 加密工具类
 */
public class EncryptUtils { 
	String encryType;
	Key key; 
	
	public EncryptUtils(String str) { 
		encryType=RemoteConfig.getEncryType();
		getKey(str);//生成密匙 
	} 
	/** 
	 * 根据参数生成KEY 
	 * 
	 * @param strKey
	 */ 
	public void getKey(String strKey) { 
		try { 
			KeyGenerator _generator = KeyGenerator.getInstance(encryType); 
			_generator.init(new SecureRandom(strKey.getBytes())); 
			this.key = _generator.generateKey();
			_generator = null; 
		} catch (Exception e) { 
    	throw new RuntimeException("Error initializing SqlMap class. Cause: " + e); 
		} 
	} 
 
	/** 
	 * 文件file进行加密并保存目标文件destFile中 
	 * 
	 * @param file   要加密的文件 如c:/test/srcFile.txt 
	 * @param destFile 加密后存放的文件名 如c:/加密后文件.txt 
	 */ 
	public void encrypt(String file, String destFile) throws Exception { 
		Cipher cipher = Cipher.getInstance(encryType); 
		cipher.init(Cipher.ENCRYPT_MODE, this.key); 
		InputStream is = new FileInputStream(file); 
		OutputStream out = new FileOutputStream(destFile); 
		CipherInputStream cis = new CipherInputStream(is, cipher); 
		byte[] buffer = new byte[1024]; 
		int r; 
		while ((r = cis.read(buffer)) > 0) { 
			out.write(buffer, 0, r); 
		} 
		cis.close(); 
		is.close(); 
		out.close(); 
	} 
	
	/**
	 * 加密密码
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String encrypt1(String str) {
		byte[] encodeBytes=Base64.getEncoder().encode(str.getBytes());
		return new String(encodeBytes);
	}
	
	/**
	 * 解密密码
	 * @param str
	 * @return
	 */
	public static String decrypt1(String str) {
		byte[] decodeBytes=Base64.getDecoder().decode(str.getBytes());
		return new String(decodeBytes);
	}
	
	/** 
	 * 文件采用DES算法解密文件 
	 * 
	 * @param file 已加密的文件 如c:/加密后文件.txt 
	 * @param destFile 解密后存放的文件名 如c:/ test/解密后文件.txt 
	 */ 
	public void decrypt(String file, String dest) throws Exception { 
		Cipher cipher = Cipher.getInstance(encryType); 
		cipher.init(Cipher.DECRYPT_MODE, this.key); 
		InputStream is = new FileInputStream(file); 
		OutputStream out = new FileOutputStream(dest); 
		
		CipherOutputStream cos = new CipherOutputStream(out, cipher); 
		byte[] buffer = new byte[1024]; 
		int r; 
		while ((r = is.read(buffer)) >= 0) { 
			cos.write(buffer, 0, r); 
		} 
		cos.close(); 
		out.close(); 
		is.close(); 
		hideFile(dest);
	} 
	
	/**
	 * 隐藏文件
	 * @param filename
	 */
	public static void hideFile(String filename) {
		String os = System.getProperty("os.name");  
		if(!os.toLowerCase().startsWith("win")){  
			return;
		}  
		File file=new File(filename);//定义文件路径
		try {   
			if(!file.exists())
				file.mkdir();//如果文件路径不存在就创建一个
			String string=" attrib +H  "+file.getAbsolutePath(); //设置文件属性为隐藏
			Runtime.getRuntime().exec(string);  //
		} catch (IOException e) {  
			
		}   
	}
	
}
