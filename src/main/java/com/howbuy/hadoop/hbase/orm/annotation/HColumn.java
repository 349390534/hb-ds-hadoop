package com.howbuy.hadoop.hbase.orm.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(FIELD)
@Retention(RUNTIME)
public @interface HColumn {
	/**
	 * The name of the column in the base. If not set then the name is taken
	 * from the class name lowerCased.
	 */
	String familyName() default "";

	String qualifierName() default "";

	boolean isQualifierList() default false;
	
	boolean isQualifierValueMap() default false;

	boolean id() default false;

}
