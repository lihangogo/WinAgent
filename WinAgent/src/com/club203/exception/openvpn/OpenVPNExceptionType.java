package com.club203.exception.openvpn;

/**
 * OpenVPN异常类型
 * @author hehaoxing
 */
public enum OpenVPNExceptionType {
	
	REPEAT_STARTUP(1, "不支持启动多个OpenVPN客户端"),
	CANNOT_EXEC(2, "客户端启动失败"),
	PROCESS_FAIL(3, "客户端进程启动失败"),
	AUTH_FAIL(4, "鉴权失败，错误的用户名或密码"),
	CONFIG_FAIL(5, "读取配置文件失败"),
	CONFIG_NOTFOUND(6, "找不到相关配置文件"),
	TAPUSED_OR_CERTERROR(7, "虚拟网卡被占用或证书错误"),
	HANDSHAKE_FAIL(8, "网络连接异常或证书不匹配"),//已废弃
	READTHREAD_IO_FAIL(9, "读取进程状态失败"),
	CONFIG_DECRYPT_FAIL(10, "配置文件不能解密"),
	
	READTHREAD_FAIL(11, "读取线程运行错误"),
	OPENVPN_TIMEOUT(12, "客户端建连超时"),
	EXECTHREAD_FAIL(13, "启动线程执行错误"),
	
	INTERRUPTED(15, "操作被中断"),
	
	UNKNOWN_ERROR(20, "发现未知异常");
	
	private Integer code;
	private String desc;
	
	private OpenVPNExceptionType(Integer code, String desc) {
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
	 * 根据OpenVPN异常编号取枚举量
	 */
	public static OpenVPNExceptionType getTypeByCode(int code) {
		for(OpenVPNExceptionType type : OpenVPNExceptionType.values()) {
			if(type.getCode() == code) {
				return type;
			}
		}
		return null;
	}
}
