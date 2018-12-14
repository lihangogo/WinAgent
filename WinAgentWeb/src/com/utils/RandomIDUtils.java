package com.utils;

import java.util.UUID;

/**
 * 自动生成主键
 * @author lihan
 */
public class RandomIDUtils {

	public static String getRandomID(){
		//生成不相同的标识符，用作主键
		String oid = UUID.randomUUID().toString().replaceAll("-", "");
		return oid;
	}
}
