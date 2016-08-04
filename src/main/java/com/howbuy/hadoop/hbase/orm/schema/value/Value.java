package com.howbuy.hadoop.hbase.orm.schema.value;

/**
 * Used by schema module 
 * @author wlu
 *
 */
public interface Value {
	public byte[] toBytes();
	
	public String getType();

}
