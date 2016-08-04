package com.howbuy.rdb.database.dao.impl;

import com.howbuy.rdb.cache.ICacheControllerService;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SqlMapClientTemplateMonitor extends SqlMapClientTemplate {

    protected ICacheControllerService getCacheClient() {
        return cacheClient;
    }

    protected void setCacheClient(ICacheControllerService cacheClient) {
        this.cacheClient = cacheClient;
    }


    private ICacheControllerService cacheClient;

	public List queryCacheForList(String statementName, int skipResults,
			int maxResults, String key, Date date) {
		// TODO Auto-generated method stub
		List list = (List) cacheClient.getCacheClient().get(key);

		if (list == null) {

			list = super.queryForList(statementName, skipResults, maxResults);

			cacheClient.getCacheClient().put(key, list, date);

		}

		return list;
	}

	public List queryCacheForList(String statementName, String key, Date date) {
		// TODO Auto-generated method stub
	    List list = (List) cacheClient.getCacheClient().get(key);

		if (list == null) {

			list = super.queryForList(statementName);

			cacheClient.getCacheClient().put(key, list, date);
		}

		return list;
	}

	public List queryCacheForList(String statementName, Object parameterObject,
			String key, Date date) {

	    List list = (List) cacheClient.getCacheClient().get(key);

		if (list == null) {

			list = super.queryForList(statementName, parameterObject);

			cacheClient.getCacheClient().put(key, list, date);
		}

		return list;

	}

	public List queryCacheForList(String statementName, Object parameterObject,
			int skipResults, int maxResults, String key, Date date) {
		// TODO Auto-generated method stub
	    List list = (List) cacheClient.getCacheClient().get(key);

		if (list == null) {

			list = super.queryForList(statementName, parameterObject,
					skipResults, maxResults);

			cacheClient.getCacheClient().put(key, list, date);
		}

		return list;
	}

	public Map queryCacheForMap(String statementName, Object parameterObject,
			String keyProperty, String valueProperty, String key, Date date) {
		// TODO Auto-generated method stub
		Map map = (Map) cacheClient.getCacheClient().get(key);

		if (map == null) {

			map = super.queryForMap(statementName, parameterObject,
					keyProperty, valueProperty);

			cacheClient.getCacheClient().put(key, map, date);
		}

		return map;
	}

	public Map queryCacheForMap(String statementName, Object parameterObject,
			String keyProperty, String key, Date date) {
		// TODO Auto-generated method stub
		Map map = (Map) cacheClient.getCacheClient().get(key);

		if (map == null) {

			map = super
					.queryForMap(statementName, parameterObject, keyProperty);

			cacheClient.getCacheClient().put(key, map, date);
		}

		return map;
	}

	public Object queryCacheForObject(String statementName,
			Object parameterObject, Object resultObject, String key, Date date) {
		// TODO Auto-generated method stub
	    Object ob = cacheClient.getCacheClient().get(key);

		if (ob == null) {

			ob = super.queryForObject(statementName, parameterObject,
					resultObject);

			cacheClient.getCacheClient().put(key, ob, date);
		}

		return ob;
	}

	public Object queryCacheForObject(String statementName,
			Object parameterObject, String key, Date date) {
		// TODO Auto-generated method stub
	    Object ob = cacheClient.getCacheClient().get(key);

		if (ob == null) {

			ob = super.queryForObject(statementName, parameterObject);

			cacheClient.getCacheClient().put(key, ob, date);
		}

		return ob;

	}

	public Object queryCacheForObject(String statementName, String key,
			Date date) {
		// TODO Auto-generated method stub
	    Object ob = cacheClient.getCacheClient().get(key);

		if (ob == null) {

			ob = super.queryForObject(statementName);

			cacheClient.getCacheClient().put(key, ob, date);
		}

		return ob;

	}

}
