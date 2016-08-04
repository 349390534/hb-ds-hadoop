/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.service.impl;

import com.howbuy.rdb.database.service.IBaseService;

import org.springframework.context.MessageSource;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ BaseServiceImpl.java, v 0.1 Apr 27, 2011 2:08:09 PM ji.ma Exp $
 * @since JDK1.6
 */
public class BaseServiceImpl implements IBaseService {

    protected MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


}
