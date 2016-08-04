package com.howbuy.rdb.database.dao.impl;

import com.howbuy.rdb.cache.ICacheControllerService;
import com.ibatis.sqlmap.client.SqlMapClient;

import org.springframework.dao.support.DaoSupport;

import javax.sql.DataSource;

public class SqlMapClientDaoSupportMonitor extends DaoSupport {

    private SqlMapClientTemplateMonitor sqlMapClientTemplate = new SqlMapClientTemplateMonitor();

    private boolean externalTemplate = false;

    /**
     * Set the JDBC DataSource to be used by this DAO.
     * Not required: The SqlMapClient might carry a shared DataSource.
     *
     * @see #setSqlMapClient
     */
    public final void setDataSource(DataSource dataSource) {
        this.sqlMapClientTemplate.setDataSource(dataSource);
    }

    /**
     * Return the JDBC DataSource used by this DAO.
     */
    public final DataSource getDataSource() {
        return (this.sqlMapClientTemplate != null ? this.sqlMapClientTemplate.getDataSource() : null);
    }

    /**
     * Set the iBATIS Database Layer SqlMapClient to work with.
     * Either this or a "sqlMapClientTemplate" is required.
     *
     * @see #setSqlMapClientTemplate
     */
    public final void setSqlMapClient(SqlMapClient sqlMapClient) {
        this.sqlMapClientTemplate.setSqlMapClient(sqlMapClient);
    }

    /**
     * Return the iBATIS Database Layer SqlMapClient that this template works with.
     */
    public final SqlMapClient getSqlMapClient() {

        return this.sqlMapClientTemplate.getSqlMapClient();
    }

    /**
     * Set the SqlMapClientTemplate for this DAO explicitly,
     * as an alternative to specifying a SqlMapClient.
     *
     * @see #setSqlMapClient
     */
    public final void setSqlMapClientTemplate(SqlMapClientTemplateMonitor sqlMapClientTemplateMonitor) {
        if (sqlMapClientTemplate == null) {
            throw new IllegalArgumentException("Cannot set sqlMapClientTemplate to null");
        }
        this.sqlMapClientTemplate = sqlMapClientTemplateMonitor;
        this.externalTemplate = true;
    }

    /**
     * Return the SqlMapClientTemplate for this DAO,
     * pre-initialized with the SqlMapClient or set explicitly.
     */
    public final SqlMapClientTemplateMonitor getSqlMapClientTemplate() {

        return sqlMapClientTemplate;
    }

    protected final void checkDaoConfig() {
        if (!this.externalTemplate) {
            this.sqlMapClientTemplate.afterPropertiesSet();
        }
    }

    /**
     * Set the iBATIS Database Layer SqlMapClient to work with.
     * Either this or a "sqlMapClientTemplate" is required.
     *
     * @see #setSqlMapClientTemplate
     */
    public final void setCacheClient(ICacheControllerService cacheClient) {
        this.sqlMapClientTemplate.setCacheClient(cacheClient);
    }

}
