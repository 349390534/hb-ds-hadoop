/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.dao.ibatis.model;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ KeyVO.java, v 0.1 Apr 25, 2011 4:55:44 PM ji.ma Exp $
 * @since JDK1.6
 */
public class KeyVO {

    private String key;

    private Object value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
