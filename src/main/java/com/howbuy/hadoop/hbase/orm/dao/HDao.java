package com.howbuy.hadoop.hbase.orm.dao;

import java.util.List;

import com.howbuy.hadoop.hbase.orm.schema.value.Value;

public interface HDao<T> {

	/**
	 * create a HBase Table according to it's annotations. <br>
	 * If the table already exits, delete and then recreate.
	 * 
	 * @param clazz
	 */
	public void createTable();

	/**
	 * create a HBase Table according to it's annotations. <br>
	 * If the table already exits, return.
	 * 
	 * @param clazz
	 */
	public void createTableIfNotExist();

	/**
	 * insert one record (row) to HBase table
	 * 
	 * @param data
	 */
	public void insert(T data);

	public void deleteById(Value rowkey);

	/**
	 * delete the whole data from HBase. (delete the row with data's rowkey)
	 * 
	 * @param data
	 */
	public void deleteById(T data);

	/**
	 * Specify field name and delete specific family:qualifier
	 * 
	 * @param data
	 * @param family
	 * @param qualifier
	 */
	public void delete(T data, String familyFieldName, String qualifierFieldName);

	/**
	 * Specify field name and delete whole specific family
	 * 
	 * @param data
	 * @param family
	 * @param qualifier
	 */
	public void delete(T data, String familyFieldName);

	/**
	 * Same as deleteById(T data)
	 * 
	 * @param data
	 */
	public void delete(T data);

	/**
	 * update the record in table according to data's id (rowkey). <br>
	 * We don't know the dirty part of the data compared to record in the table,
	 * even don't know whether the data has already exists in the table. So, we
	 * just <b>insert</b> the data to the table...
	 * 
	 * @param data
	 */
	public void update(T data);

	/**
	 * 
	 * @param data
	 * @param familyFieldName
	 */
	public void update(T data, List<String> familyFieldName);


	public T queryById(Value id);

	/**
	 * query according to the filter. For filters, such as qualifier filters, we
	 * can only get data of THAT qualifier, not data of the whole row. We need
	 * to query for the second time and return the whole object if needed.
	 * 
	 * @param filter
	 * @param returnWholeObject
	 *            whether need to return the whole object
	 * @return
	 */
	public List<T> queryWithFilter(String filter, boolean returnWholeObject);

	/**
	 * Set returnWholeObject as false for the function above
	 * 
	 * @param filter
	 * @return
	 */
	public List<T> queryWithFilter(String filter);
}
