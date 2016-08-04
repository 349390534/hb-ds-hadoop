package com.howbuy.hadoop.mapping.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 *  todo:请添加注释描述
 * </pre>
 *
 * @author ji.ma
 * @create 13-1-24 上午11:32
 * @modify
 * @since JDK1.6
 */
public class UrlInfo {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String channel;

    private String subChannel;

    private String key;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSubChannel() {
        return subChannel;
    }

    public void setSubChannel(String subChannel) {
        this.subChannel = subChannel;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "UrlInfo{" +
                "log=" + log +
                ", channel='" + channel + '\'' +
                ", subChannel='" + subChannel + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
