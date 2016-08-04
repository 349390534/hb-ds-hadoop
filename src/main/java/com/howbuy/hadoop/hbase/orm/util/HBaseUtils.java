package com.howbuy.hadoop.hbase.orm.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableFactory;
import org.apache.hadoop.hbase.client.HTableInterface;

public class HBaseUtils {

//	  private static final String QUORUM = "192.168.1.100";
//	  private static final String CLIENTPORT = "2181";
	  private static Configuration conf = null;
	  private static HConnection conn = null;
	  
	  /**
	   * 获取全局唯一的Configuration实例
	   * @return
	   */
	  public static synchronized Configuration getConfiguration()
	  {		
	    if(conf == null)
	    {
	      conf =  HBaseConfiguration.create();
//	      conf.set("hbase.zookeeper.quorum", QUORUM); 
//	      conf.set("hbase.zookeeper.property.clientPort", CLIENTPORT);			
	    }
	    return conf;				
	  }
	  
	  /**
	   * 获取全局唯一的HConnection实例
	   * @return
	   * @throws ZooKeeperConnectionException
	   */
	  public static synchronized HConnection getHConnection() throws ZooKeeperConnectionException
	  {
	    if(conn == null)
	    {
	      /*
	       * * 创建一个HConnection
	       * HConnection connection = HConnectionManager.createConnection(conf); 
	       * HTableInterface table = connection.getTable("mytable");
	       * table.get(...); ...
	       * table.close(); 
	       * connection.close();
	       * */
	      conn = HConnectionManager.createConnection(getConfiguration());
	      
	      
	    }
	    
	    return conn;
	  }
	  
//	  public void addUser(User user) throws IOException 
//	  {
//	    HTableInterface usersTable = conn.getTable(TABLE_NAME);
//	    
//	    Put put = makePut(user);		
//	    usersTable.put(put);
//	    
//	    usersTable.close();
//	    log.info("Add a User:"+user.name+" successfully");
//	  }
	  
	  
	}
