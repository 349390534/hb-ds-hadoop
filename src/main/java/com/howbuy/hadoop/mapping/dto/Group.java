
package com.howbuy.hadoop.mapping.dto;

import java.util.ArrayList;
import java.util.List;


public class Group {
    /**
     * 一级域名
     */
	private String hostKey;
	/**
	 * 项目名称
	 */
    private String pathKey; 
    /**
     * 一级渠道
     */
    private String channel;
    /**
     * 一级渠道名称
     */
    private String channelStr;

    private List<Mapping> mapping;

	public String getHostKey() {
		return hostKey==null?"":hostKey;
	}

	public void setHostKey(String hostKey) {
		this.hostKey = hostKey;
	}

	public String getPathKey() {
		return pathKey==null?"":pathKey;
	}

	public void setPathKey(String pathKey) {
		this.pathKey = pathKey;
	}

	public String getChannel() {
		return channel==null?"":channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannelStr() {
		return channelStr==null?"":channelStr;
	}

	public void setChannelStr(String channelStr) {
		this.channelStr = channelStr;
	}

	public List<Mapping> getMappings() {
	    if (mapping == null) {
            mapping = new ArrayList<Mapping>();
        }
		return mapping;
	}

	public void setMappings(List<Mapping> mapping) {
		this.mapping = mapping;
	}

	@Override
	public String toString() {
		return "Group [hostKey=" + hostKey + ", pathKey=" + pathKey
				+ ", channel=" + channel + ", channelStr=" + channelStr
				+ ", mappings=" + mapping + "]";
	}

    
    
}
