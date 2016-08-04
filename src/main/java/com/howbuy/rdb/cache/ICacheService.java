/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.cache;

import java.util.Date;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ ICacheService.java, v 0.1 2011-8-23 下午04:31:49 ji.ma Exp $
 * @since JDK1.6
 */
public interface ICacheService<K, V> {
    /**
     * <pre>
     * 保存数据到CACHE中
     * </pre>
     *
     * @param key
     * @param value
     * @return
     */
    public V put(K key, V value);

    /**
     * <pre>
     * 保存数据到CACHE，数据有效期
     * </pre>
     *
     * @param key
     * @param value
     * @param date
     * @return
     */
    public V put(K key, V value, Date date);

    /**
     * <pre>
     * 保存数据到CACHE中，有效期为秒
     * </pre>
     *
     * @param key
     * @param value
     * @param TTL
     * @return
     */
    public V put(K key, V value, Integer TTL);

    /**
     * <pre>
     * 从CACHE中获取数据
     * </pre>
     *
     * @param key
     * @return
     */
    public V get(K key);

    /**
     * <pre>
     * 将数据从CACHE移走
     * </pre>
     *
     * @param key
     * @return
     */
    public V remove(K key);

    /**
     * <pre>
     * 将CACHE中的数据替换掉
     * </pre>
     *
     * @param key
     * @param value
     * @return
     */
    public V replace(K key, V value);

    /**
     * <pre>
     * 清空CACHE
     * </pre>
     *
     * @return
     */
    public boolean flushAll();
}
