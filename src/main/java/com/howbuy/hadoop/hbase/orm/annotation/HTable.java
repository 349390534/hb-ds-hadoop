package com.howbuy.hadoop.hbase.orm.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface HTable {
	/**
	 * The name of the column in the base. If not set then the name is taken
	 * from the class name lowercased.
	 */
	String tableName() default "";

	boolean canBeFamily() default false;
}
