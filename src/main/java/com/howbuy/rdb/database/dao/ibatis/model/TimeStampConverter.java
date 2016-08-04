/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.dao.ibatis.model;

import oracle.sql.TIMESTAMP;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ TimeStampConverter.java, v 0.1 Apr 25, 2011 5:52:06 PM ji.ma Exp $
 * @since JDK1.6
 */
public class TimeStampConverter implements Converter {

    private final Logger log = Logger.getLogger(this.getClass());

    public TimeStampConverter() {

        this.defaultValue = null;
        this.useDefault = true;
    }

    public TimeStampConverter(Object defaultValue) {

        this.defaultValue = defaultValue;
        this.useDefault = true;
    }

    private Object defaultValue = null;

    private boolean useDefault = true;

    public Object convert(Class type, Object value) {

        if (value == null || "".equals(value)) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException("No value specified");
            }
        }

        if (value instanceof TIMESTAMP) {

            try {
                return (((oracle.sql.TIMESTAMP) value).timestampValue());
            } catch (SQLException e) {
                log.error("::", e);
            }
        }

        if (value instanceof Timestamp) {

            return value;
        }

        return null;
    }
}
