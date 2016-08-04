/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.exception;

import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ BusinessException.java, v 0.1 Apr 25, 2011 1:36:52 PM ji.ma Exp $
 * @since JDK1.6
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = -2598217010733719435L;

    public BusinessException() {
        super();
    }

    public BusinessException(String errCode, List<String> params) {
        super(errCode, params);
    }

    public BusinessException(String errCode, String[] params) {
        super(errCode, params);
    }

    public BusinessException(String errCode, String message) {
        super(errCode, message);
    }

    public BusinessException(String errCode) {
        super(errCode);
    }

}
