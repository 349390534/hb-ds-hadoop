package com.howbuy.hadoop.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.howbuy.hadoop.mapping.dto.Group;
import com.howbuy.hadoop.mapping.dto.Mapping;
import com.howbuy.hadoop.mapping.dto.UrlInfo;
import com.howbuy.hadoop.utils.PatternUtils;
import com.howbuy.hadoop.utils.SpringContextHolder;
import com.howbuy.hadoop.utils.URIUtils;

/**
 * <pre>
 *  url与频道及子频道的映射关系维护者
 * </pre>
 *
 * @author ji.ma
 * @create 13-1-11 下午10:21
 * @modify
 * @since JDK1.6
 */
public class MappingServer {

    private static final Logger log = LoggerFactory.getLogger(MappingServer.class);

    /**
     * 映射关系
     */
    private static Map<String, Group> mappingGroups;
    
    /**
     * 映射关系
     * channel+$+subchannel->一级频道+二级频道
     * eg:fund$index->公募首页
     */
    private static Map<String, String> mappingChannels;
    
    

    public static Map<String, Group> getMappingGroups(){
    	if (mappingGroups == null) {
            MappingReader mappingReader = SpringContextHolder.getBean("mappingReader");
            if(mappingReader!=null){
                mappingReader.reload();
            }else{
            	log.error("bean name mappingReader is not found!");
            }
        }
    	
    	return mappingGroups;
    }
    
    
    public static Map<String, String> getMappingChannels(){
    	if (mappingChannels == null) {
    		Map<String,Group> map = getMappingGroups();
            if(map!=null){
            	translateToMappingChannels(map);
            }
        }
    	
    	return mappingChannels;
    }
    
    
    /**
     * 根据url取得频道信息
     *
     * @return 频道信息
     */
    public static UrlInfo getChannel(String url) {

        log.debug("MappingServer##getChannel:url is {}", url);
        UrlInfo urlInfo = new UrlInfo();
        if (StringUtils.isEmpty(url)) {
            return urlInfo;
        }

        if (!url.startsWith("http")) {
            url = "http://" + url;
        }


        String hostKey = "";
        String pathKey = "";
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            String path = uri.getPath();
            hostKey = URIUtils.getHostDir(host);
            //www下以path的第一级目录为根
            if ("www".equals(hostKey)) {
                pathKey = URIUtils.getFirstPath(path);
            }
        } catch (URIException e) {
            log.error("{}", e);
            e.printStackTrace();
        }


        log.debug("host is {},path is {}", hostKey, pathKey);
        if (hostKey == null) {
            return urlInfo;
        }

        if (mappingGroups == null) {
        	getMappingGroups();
        }

        Group group = mappingGroups.get(hostKey + pathKey);
        if (group != null) {
            urlInfo.setChannel(group.getChannel());
//            boolean isMatch = false;
            for (Mapping mapping : group.getMappings()) {
                Pattern pattern = Pattern.compile(mapping.getUrlPattern());
                Matcher matcher = pattern.matcher(url);
                log.debug("text is {},and pattern is {}", url, mapping.getUrlPattern());

                if (matcher.find()) {
                    log.debug("url is {},and the match pattern is {}", url, mapping.getUrlPattern());
                    urlInfo.setSubChannel(mapping.getSubchannel());
                    if (mapping.getKeyPattern() != null) {
                        String matchGroup = PatternUtils.getMatchGroup(url, mapping.getKeyPattern());
                        if (!StringUtils.isEmpty(matchGroup)) {
                            //解决正则表达式需借助斜杠的问题
                            matchGroup = matchGroup.replaceAll("/", "");
                            matchGroup = matchGroup.replaceAll(".html", "");
                            matchGroup = matchGroup.replaceAll(".htm", "");
                        }
                        urlInfo.setKey(matchGroup);
                    }
                    break;
//                    if (!isMatch) {
//                        isMatch = true;
//                    } else {
//                        log.error("this url more than one match!url is {}", url);
//                    }
                }
            }
        } else {
            log.warn("the url can not get group,host+path is {}", hostKey + pathKey);
        }

        log.debug("group is {}", urlInfo);
        return urlInfo;
    }
    
    public static void setMappingGroups(Map<String, Group> map) {
        if (map != null && !map.isEmpty()) {
            mappingGroups = map;
        }
    }


    public static void translateToMappingChannels(Map<String, Group> map) {
        if (map != null && !map.isEmpty()) {
            for (Group group : map.values()) {
                if (group == null) {
                    continue;
                }
                String channel = group.getChannel();
                String channelStr = group.getChannelStr();
                if (group.getMappings().size() > 0) {
                    for (Mapping mapping : group.getMappings()) {
                        if (mappingChannels == null) {
                            mappingChannels = new HashMap<String, String>();
                        }
                        mappingChannels.put(channel + "$" + mapping.getSubchannel(), channelStr + mapping.getSubchannelStr());
                    }
                }

            }
        }
    }


}
