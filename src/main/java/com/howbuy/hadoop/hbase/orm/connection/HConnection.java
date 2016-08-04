package com.howbuy.hadoop.hbase.orm.connection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;

public class HConnection {

	private HTablePool pool;
	private HBaseAdmin admin;

    private boolean isFlushCommits = false;

    private static Configuration cfg ;

    static {
        cfg = HBaseConfiguration.create();
    }

    public HConnection(int poolSize) {
//		Configuration cfg = new Configuration();
//		cfg.set("hbase.zookeeper.quorum", zk);
//		cfg.set("hbase.zookeeper.property.clientPort", port);

        pool = new HTablePool(cfg, poolSize);
//		try {
//			admin = new HBaseAdmin(cfg);
//		} catch (MasterNotRunningException e) {
//			e.printStackTrace();
//		} catch (ZooKeeperConnectionException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * insert put to the table with name <code>tableName</code>
	 * 
	 * @param tableName
	 * @param put
	 */
	public void insert(byte[] tableName, Put put) {
		HTableInterface table =  pool.getTable(tableName);
		try {
            table.put(put);
            if(isFlushCommits){
                table.flushCommits();
            }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            try {
                pool.putTable(table);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	/**
	 * delete the whole row of table with name <code>tableName</code>
	 * 
	 * @param tableName
	 * @param delete
	 */
	public void delete(byte[] tableName,
                       org.apache.hadoop.hbase.client.Delete delete) throws IOException {
		HTable htable = (HTable) pool.getTable(tableName);
		try {
			htable.delete(delete);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pool.putTable(htable);
		}
	}

	public Result query(byte[] tableName, Get get) throws IOException {
		HTable htable = (HTable) pool.getTable(tableName);
		Result result = null;
		try {
			result = htable.get(get);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pool.putTable(htable);
		}
		return result;

	}

	public boolean tableExists(final String tableName) {
		try {
			return admin.tableExists(tableName);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void deleteTable(final String tableName) {
		try {
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createTable(HTableDescriptor td) {
		try {
			admin.createTable(td);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void setFlushCommits(boolean isFlushCommits) {
        isFlushCommits = isFlushCommits;
    }
}
