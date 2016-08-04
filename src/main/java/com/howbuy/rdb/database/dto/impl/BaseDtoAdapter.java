/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.dto.impl;

import com.howbuy.rdb.database.dao.impl.IBatisDaoUtils;
import com.howbuy.rdb.database.dto.IBaseDto;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ BaseModelAdapter.java, v 0.1 Apr 25, 2011 10:18:56 AM ji.ma Exp $
 * @since JDK1.6
 */
public abstract class BaseDtoAdapter extends BaseDtoImpl {

    /**
     * <pre>
     *
     * </pre>
     */
    private static final long serialVersionUID = -5667483166249714826L;
    public static final int FLAG_NO = 0;
    public static final int FLAG_YES = 1;

    public static final int IS_DELETE_NO = 0;
    public static final int IS_DELETE_YES = 1;

    private String patitionPrefix = null;

    public String toString() {

        StringBuffer propBuffer = new StringBuffer();

        Field[] fields = this.getClass().getDeclaredFields();

        propBuffer.append("[").append(this.getClass()).append("]");

        for (Field field : fields) {

            String fieldName = field.getName();

            Object fieldValue = null;

            String getterMethod = "get" + Character.toUpperCase(fieldName.charAt(0))
                    + fieldName.substring(1);
            try {
                Method getMethod = this.getClass().getMethod(getterMethod, (Class[]) null);

                Object o = getMethod.invoke(this, (Object[]) null);

                if (o instanceof Map || o instanceof List) {

                    continue;
                }

                if (o instanceof IBaseDto) {
                    fieldName += "Id";
                    fieldValue = IBatisDaoUtils.getPrimaryKeyValue((IBaseDto) o);
                } else {
                    fieldValue = o;
                }

                propBuffer.append(fieldName).append(":").append(fieldValue).append(",");

            } catch (Exception e) {
            }

        }

        return propBuffer.substring(0, propBuffer.length() - 1);
    }

    public String getPatitionPrefix() {
        return patitionPrefix;
    }

    public void setPatitionPrefix(String patitionPrefix) {
        this.patitionPrefix = patitionPrefix;
    }

}
