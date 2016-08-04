/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.dao;

import com.howbuy.rdb.database.dto.IBaseDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ BaseDao.java, v 0.1 Apr 25, 2011 11:04:18 AM ji.ma Exp $
 * @since JDK1.6
 */
public interface BaseDao<T extends IBaseDto, PK extends Serializable> {

    /**
     * <pre>
     *
     * </pre>
     *
     * @param object
     * @return
     */
    List<T> getAll(T object);

    /**
     * <pre>
     * get an object based on class and identifier.
     * </pre>
     *
     * @param id
     * @return a populated object
     */
    T getRow(final T object);

    /**
     * <pre>
     * Checks for existence of an object of type T using the id arg.
     * </pre>
     *
     * @param id
     * @return true if it exists, false if it doesn't
     */
    boolean exists(T object);

    /**
     * <pre>
     * Generic method to save an object
     * </pre>
     *
     * @param object
     * @return the persisted object
     */
    T save(T object);


    /**
     * <pre>
     * Generic method to save an object
     * </pre>
     *
     * @param object
     */
    Integer updatePK(T object);

    /**
     * <pre>
     *
     * </pre>
     *
     * @param object
     */
    void delete(T object);

    /**
     * <pre>
     *
     * </pre>
     *
     * @param object
     * @param listPK
     */
    public void deleteBatchLogic(T object, ArrayList<Long> listPK);

    /**
     * <pre>
     *
     * </pre>
     *
     * @param object
     * @param listPK
     */
    public void updateBatchPK(T object, List<Long> listPK);

    /**
     * <pre>
     *
     * </pre>
     *
     * @param object
     * @return
     */
    public List<T> getAllData(final T object);

    /**
     * <pre>
     *
     * </pre>
     *
     * @param object
     * @return
     */
    public T getRowData(final T object);

    /**
     * <pre>
     *
     * </pre>
     *
     * @param object
     * @return
     */
    public boolean existsData(final T object);
}
