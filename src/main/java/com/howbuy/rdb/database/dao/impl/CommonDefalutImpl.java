package com.howbuy.rdb.database.dao.impl;

import com.howbuy.rdb.database.dto.impl.BaseDtoImpl;
import com.howbuy.rdb.database.util.Constant;

/**
 * <pre>
 * save default time of transaction parameter create users and update users
 * </pre
 *
 * @author ji.ma
 * @version $ CommonDefalutImpl.java, v 0.1 2011-10-24 下午05:44:43 ji.ma Exp
 *          $
 * @since JDK1.6
 */
public class CommonDefalutImpl extends DefaultColumnImpl {

    @Override
    public void setCreateUserID(Object ob) throws Exception {

        BaseDtoImpl dtoImpl = (BaseDtoImpl) ob;

        Object[] args = null;

        args = new Object[]{dtoImpl.getCreateUser()};

        if (dtoImpl.getCreateUser() == null) {
            args = new Object[]{new Long(1L)};
        }

        setMethodValue(ob, Constant.DEFAULT_COLUMN_METHOD_CREATEUSER, args);
    }

    @Override
    public void setUpdateUserID(Object ob) throws Exception {

        BaseDtoImpl dtoImpl = (BaseDtoImpl) ob;

        Object[] args = null;
        args = new Object[]{dtoImpl.getUpdateUser()};
        if (dtoImpl.getUpdateUser() == null) {
            args = new Object[]{new Long(1L)};
        }
        setMethodValue(ob, Constant.DEFAULT_COLUMN_METHOD_UPDATEUSER, args);
    }

}
