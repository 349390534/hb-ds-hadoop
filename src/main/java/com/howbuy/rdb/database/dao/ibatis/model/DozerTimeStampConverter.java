package com.howbuy.rdb.database.dao.ibatis.model;

import oracle.sql.TIMESTAMP;
import org.dozer.CustomConverter;
import org.dozer.converters.ConversionException;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ DozerTimeStampConverter.java, v 0.1 2011-6-29 下午04:16:04 ji.ma Exp $
 * @since JDK1.6
 */
public class DozerTimeStampConverter implements CustomConverter {

    public DozerTimeStampConverter() {

        this.defaultValue = null;
        this.useDefault = true;
    }

    public DozerTimeStampConverter(Object defaultValue) {

        this.defaultValue = defaultValue;
        this.useDefault = true;
    }

    private Object defaultValue = null;

    private boolean useDefault = true;

    @Override
    public Object convert(Object arg0, Object arg1, Class<?> arg2, Class<?> arg3) {

        if (arg1 == null || "".equals(arg1)) {
            if (useDefault) {
                return (defaultValue);
            } else {
                throw new ConversionException("No value specified", null);
            }
        }

        if (arg1 instanceof TIMESTAMP) {

            try {
                return (((oracle.sql.TIMESTAMP) arg1).timestampValue());
            } catch (SQLException e) {
            }
        }

        if (arg1 instanceof Timestamp) {
            return arg1;
        }
        return null;
    }
}
