package com.club203.exception.socks5;

public class ShadowsocksException extends Exception {

	private ShadowsocksExceptionType type;
	
	public ShadowsocksException(ShadowsocksExceptionType type) {
		super(type.toString());
		this.type = type;
	}
	
	public ShadowsocksException(int errorCode) {
		super(ShadowsocksExceptionType.getTypeByCode(errorCode).toString());
		this.type = ShadowsocksExceptionType.getTypeByCode(errorCode);
	}

	public int getErrorCode() {
		return type.getCode();
	}
	
	public String getDesc() {
		return type.getDesc();
	}
	
	@Override
	public String toString() {
		return "Socks5: " + getDesc();
	}
}
