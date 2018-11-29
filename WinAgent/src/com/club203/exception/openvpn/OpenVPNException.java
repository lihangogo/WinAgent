package com.club203.exception.openvpn;

public class OpenVPNException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private OpenVPNExceptionType type;
	
	public OpenVPNException(OpenVPNExceptionType type) {
		super(type.toString());
		this.type = type;
	}
	
	public OpenVPNException(int errorCode) {
		super(OpenVPNExceptionType.getTypeByCode(errorCode).toString());
		this.type = OpenVPNExceptionType.getTypeByCode(errorCode);
	}

	public int getErrorCode() {
		return type.getCode();
	}
	
	public String getDesc() {
		return type.getDesc();
	}
	
	@Override
	public String toString() {
		return "OpenVPN: " + getDesc();
	}
}
