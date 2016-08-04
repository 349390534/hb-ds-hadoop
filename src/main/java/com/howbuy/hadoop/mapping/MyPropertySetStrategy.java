package com.howbuy.hadoop.mapping;

import net.sf.json.JSONException;
import net.sf.json.util.PropertySetStrategy;

public class MyPropertySetStrategy extends PropertySetStrategy {

	public MyPropertySetStrategy(PropertySetStrategy ori) {
		this.ori = ori;
	}

	private final PropertySetStrategy ori;
	
	public void setProperty(Object o, String n, Object v)
			throws JSONException {
		try{
		ori.setProperty(o, n, v);
		}catch(Exception e){
			//ignore
		}
	}

}
