package com.club203.utils;

import java.util.UUID;

public class DBUtils {

	/**
	 * 获取UUID
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "").toLowerCase();
	}
}
