/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.database.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ Pagination.java, v 0.1 Apr 27, 2011 1:34:16 PM ji.ma Exp $
 * @since JDK1.6
 */
public class Pagination extends BaseListDto {

    private static final long serialVersionUID = -2347438840805341687L;

    @SuppressWarnings("unchecked")
    private List resultList = new ArrayList();

    @SuppressWarnings("unchecked")
    public List getResultList() {
        return resultList;
    }

    @SuppressWarnings("unchecked")
    public void setResultList(List resultList) {
        this.resultList = resultList;
    }

}
