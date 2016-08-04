/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.dto.impl;

import com.howbuy.rdb.database.dto.IBaseDto;

import java.sql.Timestamp;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ BaseDtolImpl.java, v 0.1 Apr 25, 2011 10:17:30 AM ji.ma Exp $
 * @since JDK1.6
 */
public abstract class BaseDtoImpl implements IBaseDto {

    private static final long serialVersionUID = 3176448148012423247L;

    private Long createUser;

    private Timestamp createTime;

    private Long updateUser;

    private Timestamp updateTime;

    private Long isRemove;

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public Long getIsRemove() {
        return isRemove;
    }

    public void setIsRemove(Long isRemove) {
        this.isRemove = isRemove;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

}
