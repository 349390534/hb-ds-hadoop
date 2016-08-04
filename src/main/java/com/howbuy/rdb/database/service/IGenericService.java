/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.service;

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
 * @version $ IGenericService.java, v 0.1 Apr 27, 2011 2:02:49 PM ji.ma Exp $
 * @since JDK1.6
 */
public interface IGenericService<T extends IBaseDto, PK extends Serializable> extends IBaseService {

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
     *
     * </pre>
     *
     * @param object
     * @return
     */
    List<T> getAllData(T object);

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
     * get an object based on class and identifier.
     * </pre>
     *
     * @param id
     * @return a populated object
     */
    T getRowData(final T object);

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
     * Checks for existence of an object of type T using the id arg.
     * </pre>
     *
     * @param id
     * @return true if it exists, false if it doesn't
     */
    boolean existsData(T object);

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
    void updatePK(T object);

    /**
     * <pre>
     *
     * </pre>
     *
     * @param object
     * @param listPK
     */
    void updateBatchPK(T object, List<Long> listPK);

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
     */
    void deleteBatchLogic(T object, ArrayList listPK);

}
