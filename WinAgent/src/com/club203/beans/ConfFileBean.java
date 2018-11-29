package com.club203.beans;

public class ConfFileBean {
	/**
	 * 配置文件的代号
	 */
	private String confFileName;
	/**
	 * 配置文件的版本号
	 */
	private String confFileVersion;
	/**
	 * 配置文件的存放路径
	 */
	private String confFilePath;
	
	public ConfFileBean() {
		
	}
	
	public ConfFileBean(String confFileName, String confFileVersion, String confFilePath) {
		super();
		this.confFileName = confFileName;
		this.confFileVersion = confFileVersion;
		this.confFilePath = confFilePath;
	}
	
	public String getConfFileName() {
		return confFileName;
	}
	
	public void setConfFileName(String confFileName) {
		this.confFileName = confFileName;
	}
	
	public String getConfFileVersion() {
		return confFileVersion;
	}
	
	public void setConfFileVersion(String confFileVersion) {
		this.confFileVersion = confFileVersion;
	}
	
	public String getConfFilePath() {
		return confFilePath;
	}
	
	public void setConfFilePath(String confFilePath) {
		this.confFilePath = confFilePath;
	}

	@Override
	public String toString() {
		return "ConfFileBean [confFileName=" + confFileName + ", confFileVersion=" + confFileVersion + ", confFilePath="
				+ confFilePath + "]";
	}

}
