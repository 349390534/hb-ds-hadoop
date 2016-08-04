/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.dao.impl;

import com.howbuy.rdb.database.dao.IDefaultColumnManage;
import com.howbuy.rdb.database.dto.impl.BaseDtoImpl;
import com.howbuy.rdb.database.util.Constant;

import org.apache.log4j.Logger;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ DefaultColumnImpl.java, v 0.1 May 9, 2011 1:27:03 PM ji.ma Exp $
 * @since JDK1.6
 */
public abstract class DefaultColumnImpl implements IDefaultColumnManage {

    private final Logger logger = Logger.getLogger(this.getClass());

    public void setUpdateTime(Object ob) throws Exception {

        BaseDtoImpl dtoImpl = (BaseDtoImpl) ob;

        Date date = new Date();

        logger.debug("dtoImpl.getUpdateTime():" + dtoImpl.getUpdateTime());
        Object[] args = new Object[]{dtoImpl.getUpdateTime() == null ? new Timestamp(date
                .getTime()) : dtoImpl.getUpdateTime()};

        setMethodValue(ob, Constant.DEFAULT_COLUMN_METHOD_UPDATETIME, args);
    }

    public void setCreateTime(Object ob) throws Exception {

        BaseDtoImpl dtoImpl = (BaseDtoImpl) ob;

        Date date = new Date();

        logger.debug("dtoImpl.getUpdateTime():" + dtoImpl.getCreateTime());

        Object[] args = new Object[]{dtoImpl.getCreateTime() == null ? new Timestamp(date
                .getTime()) : dtoImpl.getCreateTime()};

        setMethodValue(ob, Constant.DEFAULT_COLUMN_METHOD_CREATETIME, args);
    }

    @Override
    public void setDefaultColumn(Object ob, String updateType) throws Exception {

        if (updateType == null) {

            return;
        }

        if (Constant.DEFAULT_COLUMN_TYPE_UPDATE.equals(updateType)) {

            setUpdateDefaultColumn(ob);

        } else if (Constant.DEFAULT_COLUMN_TYPE_INSERT.equals(updateType)) {

            setInsertDefaultColumn(ob);

        } else if (Constant.DEFAULT_COLUMN_TYPE_LOGICDELETE.equals(updateType)) {

            setLogicDeleteDefaultColumn(ob);
        } else if (Constant.DEFAULT_COLUMN_TYPE_lOGICSELECT.equals(updateType)) {

            setIsRemove(ob, Constant.DEFAULT_ZERO);
        }

    }

    private void setInsertDefaultColumn(Object ob) throws Exception {

        setCreateUserID(ob);

        setUpdateUserID(ob);

        setUpdateTime(ob);

        setCreateTime(ob);

        setIsRemove(ob, Constant.DEFAULT_ZERO);
    }

    private void setUpdateDefaultColumn(Object ob) throws Exception {

        setUpdateUserID(ob);

        setUpdateTime(ob);
    }

    private void setLogicDeleteDefaultColumn(Object ob) throws Exception {

        clearChildObject(ob);

        setIsRemove(ob, Constant.DEFAULT_ONE);

        setUpdateDefaultColumn(ob);
    }

    public void setMethodValue(Object ob, String methodName, Object[] args) throws Exception {

        Class cls = ob.getClass().getSuperclass().getSuperclass();

        Class[] argclass = new Class[args.length];

        for (int i = 0, j = argclass.length; i < j; i++) {

            argclass[i] = args[i].getClass();
        }

        Method method = cls.getMethod(methodName, argclass);

        method.invoke(ob, args);

    }

    private void setIsRemove(Object ob, Integer value) throws Exception {

        Object[] args = new Object[]{new Long(value)};

        setMethodValue(ob, Constant.DEFAULT_COLUMN_METHOD_ISREMOVE, args);
    }

    /**
     * <pre>
     *
     * </pre>
     *
     * @param ob
     */
    private void clearChildObject(Object ob) {

        try {
            PropertyDescriptor pd;

            BeanInfo beanInfo = Introspector.getBeanInfo(ob.getClass());

            PropertyDescriptor[] field = beanInfo.getPropertyDescriptors();

            for (int i = 0; i < field.length; i++) {

                PropertyDescriptor f = field[i];

                if (f.getName().toUpperCase().equals("SERIALVERSIONUID")) {

                    continue;

                }

                if (f.getReadMethod() == null) {

                    continue;

                }

                String type = f.getPropertyType().getCanonicalName();

                if (!CleanoutType(type)) {

                    continue;
                }

                Object[] args = new Object[]{null};

                f.getWriteMethod().invoke(ob, args);
            }

        } catch (Exception e) {
            logger.error("", e);
        }

    }

    private boolean CleanoutType(String type) {

        if (type.equals("java.lang.Long") || type.equals("java.lang.String")
                || type.equals("java.lang.Integer") || type.equals("java.lang.Double")
                || type.equals("java.sql.Timestamp") || type.equals("java.math.BigDecimal")
                || type.equals("java.util.Date") || type.equals("java.sql.Date")) {

            return true;

        }

        return false;

    }

    public abstract void setCreateUserID(Object ob) throws Exception;

    public abstract void setUpdateUserID(Object ob) throws Exception;

}
