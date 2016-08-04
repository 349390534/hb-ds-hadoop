package com.howbuy.hadoop.mapping;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertySetStrategy;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.howbuy.hadoop.mapping.dto.Group;
import com.howbuy.hadoop.mapping.dto.Mapping;
import com.howbuy.hadoop.utils.HttpUtil;

/**
 * <pre>
 * url与频道及子频道的映射关系扫描者
 * </pre>
 *
 * @author ji.ma
 * @create 13-1-10 下午12:52
 * @modify
 * @since JDK1.6
 */
public class MappingReader {


    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String urlChannel;


    /**
     * 重新载入
     */
    public void reload() {

        log.debug("-------------new reload!");
        Map<String, Group> groupMappings = new HashMap<String, Group>();
        Map<String, Group> mappings;
        try {
            String result = HttpUtil.sendPost(urlChannel, "");
            

            JSONObject jsonObj = JSONObject.fromObject(result);
            
            Iterator<String> it =  jsonObj.keys();
            
            while(it.hasNext()){
	           	 String groupstr = jsonObj.getString(it.next());
	           	 Map<String, Class<?>> classMap = new HashMap<String, Class<?>>(); 
	             classMap.put("mappings", Mapping.class); 
	             JsonConfig config = new JsonConfig();
	             config.setRootClass(Group.class);
	             config.setClassMap(classMap);
	             config.setPropertySetStrategy(new MyPropertySetStrategy(PropertySetStrategy.DEFAULT));
	             Group group = (Group)JSONObject.toBean(JSONObject.fromObject(groupstr),config);
	             groupMappings.put(group.getHostKey() + group.getPathKey(), group);
            }
            

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        log.info("####### groupMappings:{}", groupMappings);

        if (!groupMappings.isEmpty()) {
            MappingServer.setMappingGroups(groupMappings);

            //翻译到中文含义
            //eg:fundindex->公募首页
            MappingServer.translateToMappingChannels(groupMappings);
        }

    }

    public void setUrlChannel(String urlChannel) {
        this.urlChannel = urlChannel;
    }
    
    
    public static void main(String[] args){
    	 String result = HttpUtil.sendPost("http://10.70.70.23:11080/nginxMonitor/urlchannel/geturlChannelistjson.htm", "");
         
         JSONObject jsonObj = JSONObject.fromObject(result);
         
         
         
        Iterator<String> it =  jsonObj.keys();
        
 
        String name;
        String str;
         while(it.hasNext()){
        	 name = it.next();
        	 System.out.println("key:"+name);
        	 str = jsonObj.getString(name);
        	 System.out.println("groupstr:" + str);
        	 Map<String, Class<?>> classMap = new HashMap<String, Class<?>>(); 
             classMap.put("mappings", Mapping.class); 
             JsonConfig config = new JsonConfig();
             config.setRootClass(Group.class);
             config.setClassMap(classMap);
             config.setPropertySetStrategy(new MyPropertySetStrategy(PropertySetStrategy.DEFAULT));
             Group group = (Group)JSONObject.toBean(JSONObject.fromObject(str),config);
             System.out.println(ToStringBuilder.reflectionToString(group));
         }
         
        /* Map<String, Class<?>> classMap = new HashMap<String, Class<?>>(); 
         classMap.put("mappings", Mapping.class); 
        Group groups = (List<Group>)JSONObject.toBean(jsonObj, Group.class, classMap);
         
         for(Group group : groups){
        	 System.out.println("gk:" + group.getHostKey() + ",gp:" + group.getPathKey());
         }*/
    }

}
