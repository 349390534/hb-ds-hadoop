package com.howbuy.hadoop.hbase.orm.schema.value;

public class NullValue implements Value{

	@Override
	public byte[] toBytes() {
		return null;
	}

	@Override
	public String getType() {
		return "Null Value";
	}

}
