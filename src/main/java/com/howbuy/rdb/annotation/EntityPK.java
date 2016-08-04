/**
 * EasyRewards.com Inc.
 * Copyright (c) 2008-2010 All Rights Reserved.
 */
package com.howbuy.rdb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *
 * </pre>
 *
 * @author ji.ma
 * @version $ EntityPK.java, v 0.1 Apr 25, 2011 1:16:36 PM ji.ma Exp $
 * @since JDK1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityPK {

    String Pk();

    boolean defaultColumn() default true;

    String tableName();
}
