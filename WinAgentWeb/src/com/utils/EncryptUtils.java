package com.utils;

import java.util.Base64;

/**
 * 用于加密解密的工具类
 */
public class EncryptUtils {

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

}
