package com.howbuy.rdb.database.util;

import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作Bean的工具类
 *
 * @author Alex
 */
@SuppressWarnings("unchecked")
public class BeanUtil {

    private static final Logger logger = Logger.getLogger(BeanUtil.class);

    /**
     * 将Bean转换成map
     *
     * @param obj
     * @return
     */
    public static Map reflectMap(Object obj) {
        Map map = new HashMap();
        try {
            Method m[] = obj.getClass().getMethods();
            for (int i = 0; i < m.length; i++) {
                String methodName = m[i].getName();
                if (methodName.indexOf("get") == 0 && methodName.length() > 3) {
                    map.put(methodName.substring(3, 4).toLowerCase() + methodName.substring(4), m[i].invoke(obj,
                            new Object[0]));
                }
            }
        } catch (Throwable e) {
            logger.error("", e);
            throw new RuntimeException();
        }
        return map;
    }
}
