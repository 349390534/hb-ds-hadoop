
package com.howbuy.hadoop.mapping.dto;


public class Mapping {

	/**
	 * url匹配规则
	 */
    private String urlPattern;
    /**
     * 关键字匹配规则
     */
    private String keyPattern;
    /**
     * 二级渠道
     */
    private String subchannel;
    /**
     * 二级渠道名称
     */
    private String subchannelStr;
    /**
     * 三级渠道
     */
    private String sedchannel;
    /**
     * 三级渠道名称
     */
    private String sedchannelStr;
	public String getUrlPattern() {
		return urlPattern==null?"":urlPattern;
	}
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
	public String getKeyPattern() {
		return keyPattern==null?"":keyPattern;
	}
	public void setKeyPattern(String keyPattern) {
		this.keyPattern = keyPattern;
	}
	public String getSubchannel() {
		return subchannel==null?"":subchannel;
	}
	public void setSubchannel(String subchannel) {
		this.subchannel = subchannel;
	}
	public String getSubchannelStr() {
		return subchannelStr==null?"":subchannelStr;
	}
	public void setSubchannelStr(String subchannelStr) {
		this.subchannelStr = subchannelStr;
	}
	public String getSedchannel() {
		return sedchannel==null?"":sedchannel;
	}
	public void setSedchannel(String sedchannel) {
		this.sedchannel = sedchannel;
	}
	public String getSedchannelStr() {
		return sedchannelStr==null?"":sedchannelStr;
	}
	public void setSedchannelStr(String sedchannelStr) {
		this.sedchannelStr = sedchannelStr;
	}
	@Override
	public String toString() {
		return "Mapping [urlPattern=" + urlPattern + ", keyPattern="
				+ keyPattern + ", subchannel=" + subchannel
				+ ", subchannelStr=" + subchannelStr + ", sedchannel="
				+ sedchannel + ", sedchannelStr=" + sedchannelStr + "]";
	}
	
}
