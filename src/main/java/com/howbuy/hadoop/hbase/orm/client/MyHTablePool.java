package com.howbuy.hadoop.hbase.orm.client;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * A rewrite pool of HTable instances.<p>
 *
 * Each HTablePool acts as a pool for all tables.  To use, instantiate an
 * HTablePool and use {@link #getHTable(String)} to get an HTable from the pool.
 * Once you are done with it, return it to the pool with {@link #putHTableBack(org.apache.hadoop.hbase.client.HTableInterface)} (HTableInterface)}.
 *
 * <p>A pool can be created with a <i>maxSize</i> which defines the most HTable
 * references that will ever be retained for each table.  Otherwise the default
 * is {@link Integer#MAX_VALUE}.
 *
 * <p>Pool will manage its own cluster to the cluster. See {@link org.apache.hadoop.hbase.client.HConnectionManager}.
 *
 * @author greatwqs
 * @update 2012-08-25
 */
public class MyHTablePool {

    public final static int DEFAULT_POOL_SIZE   = 4;

    /**
     * ConcurrentMap<String, LinkedList<HTableInterface>>
     * String tableName
     * LinkedList<HTableInterface> the HTable pool contains HTableInterface
     * LinkedList you can create HTable pool different size you want.
     */
    private final ConcurrentMap<String, LinkedList<HTableInterface>> tables
            = new ConcurrentHashMap<String, LinkedList<HTableInterface>>();
    /***
     * Configuration for hbase-site.xml
     */
    private final Configuration config;
    /**
     * HTableInterfaceFactory that createHTableInterface and releaseHTableInterface
     */
    private final HTableInterfaceFactory tableFactory;

    /**
     * Default Constructor.
     */
    public MyHTablePool() {
        this(HBaseConfiguration.create());
    }

    /**
     * Constructor to set maximum versions and use the specified configuration.
     * @param config configuration
     */
    public MyHTablePool(final Configuration config) {
        this(config, null);
    }

    public MyHTablePool(final Configuration config, final HTableInterfaceFactory tableFactory) {
        // Make a new configuration instance so I can safely cleanup when
        // done with the pool.
        this.config = config == null ? new Configuration() : new Configuration(
                config);
        this.tableFactory = tableFactory == null ? new HTableFactory()
                : tableFactory;
    }

    /**
     * Create all the HTable instances , belonging to the given table.
     * <p>
     * Note: this is a 'create' of the given table pool.
     * @param tableName
     * @param maxSize
     * @param isAutoFlush
     */
    public void createHTablePool(final String tableName, final int maxSize, boolean isAutoFlush) {
        LinkedList<HTableInterface> queue = tables.get(tableName);
        if (queue == null) {
            queue = new LinkedList<HTableInterface>();
            tables.putIfAbsent(tableName, queue);
        }
        synchronized (queue) {
            int addHTableSize = maxSize - queue.size();
            if(addHTableSize <= 0){
                return;
            }
            for(int i=0; i<addHTableSize; i++){
                HTable table = (HTable)createHTable(tableName);
                if(table != null){
                    table.setAutoFlush(isAutoFlush);
                    queue.add(table);
                }
            }
        }
    }

    /**
     * Create all the HTable instances , belonging to the given tables.
     * <p>
     * Note: this is a 'create' of the given table pool.
     * @param tableNameArray
     * @param maxSize
     * @param isAutoFlush default false
     * usage example:
     * false: when {@link org.apache.hadoop.hbase.client.Put} use. use buffere put. call flushCommits after a time.
     *        you can design a thread(such as 3MS run a time)to loop all pool table, and call flushCommits.
     *        the performance well.
     * true: when {@link org.apache.hadoop.hbase.client.Scan} and {@link org.apache.hadoop.hbase.client.Delete} use.
     */
    public void createHTablePool(final String[] tableNameArray, final int maxSize, boolean isAutoFlush) {
        for(String tableName : tableNameArray){
            createHTablePool(tableName,maxSize,isAutoFlush);
        }
    }

    /**
     * Create all the HTable instances , belonging to the given tables.
     * <p>
     * Note: this is a 'create' of the given table pool.
     * @param maxSize
     */
    public void createHTablePool(final String[] tableNameArray, final int maxSize) {
        createHTablePool(tableNameArray,maxSize,false);
    }

    /**
     * Get a reference to the specified table from the pool.<p>
     *
     * @param tableName table name
     * @return a reference to the specified table
     * @throws RuntimeException if there is a problem instantiating the HTable
     */
    public HTableInterface getHTable(String tableName) {
        LinkedList<HTableInterface> queue = tables.get(tableName);
        if (queue == null) {
            throw new RuntimeException("There is no pool for the HTable");
        }
        HTableInterface table;
        synchronized (queue) {
            table = queue.poll();
        }
        return table;
    }

    /**
     * Get a reference to the specified table from the pool.<p>
     *
     * Create a new one if one is not available.
     * @param tableName table name
     * @return a reference to the specified table
     * @throws RuntimeException if there is a problem instantiating the HTable
     */
    public HTableInterface getHTable(byte[] tableName) {
        return getHTable(Bytes.toString(tableName));
    }

    /**
     * Puts the specified HTable back into the pool.
     * <p>
     *
     * If the HTable not belong to HTablePool before, do not use this method.
     *
     * @param table table
     */
    public void putHTableBack(HTableInterface table) {
        LinkedList<HTableInterface> queue = tables.get(Bytes.toString(table.getTableName()));
        synchronized (queue) {
            queue.add(table);
        }
    }

    protected HTableInterface createHTable(String tableName) {
        return this.tableFactory.createHTableInterface(config, Bytes
                .toBytes(tableName));
    }

    /**
     * Closes all the HTable instances , belonging to the given table, in the table pool.
     * <p>
     * Note: this is a 'shutdown' of the given table pool and different from
     * {@link #putHTableBack(org.apache.hadoop.hbase.client.HTableInterface)} Table(HTableInterface)}, that is used to return the table
     * instance to the pool for future re-use.
     *
     * @param tableName
     */
    public void closeHTablePool(final String tableName) {
        Queue<HTableInterface> queue = tables.get(tableName);
        synchronized (queue) {
            HTableInterface table = queue.poll();
            while (table != null) {
                try {
                    this.tableFactory.releaseHTableInterface(table);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                table = queue.poll();
            }
        }
        HConnectionManager.deleteConnection(this.config, true);
    }

    /**
     * See {@link #closeHTablePool(String)}.
     *
     * @param tableName
     */
    public void closeHTablePool(final byte[] tableName) {
        closeHTablePool(Bytes.toString(tableName));
    }

    /**
     * See {@link #closeHTablePool(String)}.
     *
     */
    public void closeHTablePool() {
        for(String tabName:tables.keySet()){
            closeHTablePool(tabName);
        }
    }

    /**
     * getCurrentPoolSize
     * @param tableName
     * @return
     */
    public int getCurrentPoolSize(String tableName) {
        Queue<HTableInterface> queue = tables.get(tableName);
        synchronized (queue) {
            return queue.size();
        }
    }

    public void flushCommitsHTable(final String tableName){
        Queue<HTableInterface> queue = tables.get(tableName);
        synchronized (queue){
            for (HTableInterface table : queue) {
                try {
                    table.flushCommits();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}