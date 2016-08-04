package com.howbuy.hbase;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;
import org.junit.Test;
import org.mortbay.jetty.security.SSORealm;


public class HBaseCreater {
	
	@Test
	public void testRowkey(){
		
		
		long currentId = 1L;
		byte [] rowkey = Bytes.add(MD5Hash.getMD5AsHex(Bytes.toBytes(currentId)).substring(0, 8).getBytes(),
		                    Bytes.toBytes(currentId));
		
		System.out.println(rowkey.length);
	}

}
