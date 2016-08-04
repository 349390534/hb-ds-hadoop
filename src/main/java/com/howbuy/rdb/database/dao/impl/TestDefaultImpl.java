/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.dao.impl;

import com.howbuy.rdb.database.util.Constant;


/**
 * <pre>
 * 临时的
 * </pre>
 *
 * @author ji.ma
 * @version $ TestDefaultImpl.java, v 0.1 May 10, 2011 9:21:53 AM ji.ma Exp $
 * @since JDK1.6
 */
public class TestDefaultImpl extends DefaultColumnImpl {

    @Override
    public void setCreateUserID(Object ob) throws Exception {

        Object[] args = new Object[]{new Long(1)};

        setMethodValue(ob, Constant.DEFAULT_COLUMN_METHOD_CREATEUSER, args);
    }

    @Override
    public void setUpdateUserID(Object ob) throws Exception {

        Object[] args = new Object[]{new Long(1)};

        setMethodValue(ob, Constant.DEFAULT_COLUMN_METHOD_UPDATEUSER, args);
    }

}
