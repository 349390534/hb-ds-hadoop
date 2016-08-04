/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.dao.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibatis.sqlmap.client.SqlMapClient;


/**
 * ibatis基类
 * @author yichao.song
 *
 */
public abstract class BaseDaoImpl extends  SqlMapClientDaoSupportMonitor {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "sqlMapClient")
    private SqlMapClient sqlMapClient;

//    @Autowired(required = false)
//    @Qualifier("cacheClient")
//    private ICacheControllerService cacheClient;

    @PostConstruct
    public void initSqlMapClient() {
        super.setSqlMapClient(sqlMapClient);
//        super.setCacheClient(cacheClient);
    }
    
    
     
}
