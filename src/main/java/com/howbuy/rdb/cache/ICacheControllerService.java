package com.howbuy.rdb.cache;
/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */


/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ ICacheControllerService.java, v 0.1 2011-8-24 下午01:33:26 ji.ma Exp $
 * @since JDK1.6
 */
public interface ICacheControllerService<T extends ICacheService<?, ?>> {

    /**
     * <pre>
     * 配置Cache系统配置文件
     * </pre>
     *
     * @param configFile
     */
    public void setConfigFile();

    /**
     * 重新载入Cache配置文件
     *
     * @param configFile
     */
    public void reload(String configFile);

    /**
     * 获取Cache客户端
     *
     * @param name
     * @return
     */
    public T getCacheClient();

    /**
     * <pre>
     * 初始化CACHE信息
     * </pre>
     */
    public void InitConfig();

}
