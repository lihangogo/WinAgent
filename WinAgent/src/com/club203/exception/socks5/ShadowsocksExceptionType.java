package com.club203.exception.socks5;

/**
 * Socks5 - shadowsocks5异常类型
 * @author hehaoxing
 */
public enum ShadowsocksExceptionType {

	REPEAT_STARTUP(1, "不支持打开多个Socks5代理"),
	LOCAL_PORT_IN_USE(2, "本地1080端口被占用"),
	NO_USERINFO_FOUND(3, "此用户名不存在"),
	POST_FAIL(4, "远端服务器连接失败"),//访问RestAPI失败
	WRONG_RESPONSE_FORMAT(5, "获取用户信息失败"),
	REMOTE_PORT_UNUSABLE(6, "远端服务器连接失败"),//服务器端口不可用
	
	EXECTHREAD_FAIL(11, "代理启动失败"),
	INTERRUPTED(12, "操作被中断");
	
	private Integer code;
	private String desc;
	
	private ShadowsocksExceptionType(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		return "错误码:" + this.code + "," + desc;
	}
	
	/**
	 * 根据socks5-shadowsocks异常编号，取枚举量
	 */
	public static ShadowsocksExceptionType getTypeByCode(int code) {
		for(ShadowsocksExceptionType type : ShadowsocksExceptionType.values()) {
			if(type.getCode() == code) {
				return type;
			}
		}
		return null;
	}
}
