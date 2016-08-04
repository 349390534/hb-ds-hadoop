package com.howbuy.hadoop.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 *  todo:请添加注释描述
 * </pre>
 *
 * @author ji.ma
 * @create 13-3-18 下午4:45
 * @modify
 * @since JDK1.6
 */
public class PageViewChain extends UDF{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private String lastKey;

    private String lastUrl;

    public String evaluate(final String key, final String url){
        if ( !key.equalsIgnoreCase(this.lastKey) ) {
            this.lastKey = key;
            lastUrl = "";
        }
        String toUrl = lastUrl;
        lastUrl = url;
        return toUrl;
    }
}
