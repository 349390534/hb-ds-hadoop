/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.service.impl;

import com.howbuy.rdb.database.dao.BaseDao;
import com.howbuy.rdb.database.dto.IBaseDto;
import com.howbuy.rdb.database.service.IGenericService;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ GenericServiceImpl.java, v 0.1 Apr 27, 2011 2:09:50 PM ji.ma Exp $
 * @since JDK1.6
 */
public abstract class GenericServiceImpl<T extends IBaseDto, PK extends Serializable> extends
        BaseServiceImpl
        implements
        IGenericService<T, PK> {

    private final Logger logger = Logger.getLogger(this.getClass());

    private BaseDao<T, PK> baseDao;

    public GenericServiceImpl() {

    }

    public GenericServiceImpl(BaseDao<T, PK> baseDao) {
        this.baseDao = baseDao;
    }


    public void delete(T object) {

        getBaseDao().delete(object);
    }

    public void deleteBatchLogic(T object, ArrayList listPK) {

        getBaseDao().deleteBatchLogic(object, listPK);
    }


    public boolean exists(T object) {

        return getBaseDao().exists(object);

    }

    public boolean existsData(T object) {

        return getBaseDao().existsData(object);

    }

    public List<T> getAll(T object) {
        return getBaseDao().getAll(object);
    }

    public List<T> getAllData(T object) {
        return getBaseDao().getAllData(object);
    }


    public T getRow(T object) {
        return getBaseDao().getRow(object);
    }

    public T getRowData(T object) {
        return getBaseDao().getRowData(object);
    }

    public T save(T object) {
        return getBaseDao().save(object);
    }


    public void updatePK(T object) {
        getBaseDao().updatePK(object);
    }

    public void updateBatchPK(T object, List<Long> listPK) {

        getBaseDao().updateBatchPK(object, listPK);
    }

    public abstract BaseDao<T, PK> getBaseDao();

}
