package com.howbuy.hadoop.hdfs;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HDFSWriter {
	
	private static Logger logger = LoggerFactory.getLogger(HDFSWriter.class);

	private static Configuration conf = new Configuration();
	
	
	public void write(String content,String path) throws IOException{
		
		
		FileSystem filesys = FileSystem.get(conf);
		
		Path hpath = new Path(path);
		
		FSDataOutputStream os = null;
		
		if(filesys.exists(hpath)){
	    	 logger.debug("path already exist");
	    	 os = filesys.append(hpath);
	     }else{
	    	 logger.debug("path create");
	    	 os = filesys.create(hpath);
	     }
		
		os.write(content.getBytes("utf-8"));
		
		filesys.close();
	}
}
