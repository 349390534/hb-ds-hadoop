package com.howbuy.rdb.database.dao.ibatis;

import com.howbuy.rdb.database.dao.ibatis.model.KeyVO;
import com.howbuy.rdb.database.dao.impl.IBatisDaoUtils;
import com.howbuy.rdb.database.dto.IBaseDto;
import com.howbuy.rdb.database.dto.impl.BaseDtoImpl;
import com.howbuy.rdb.factory.SpringContextUtil;

import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DynamicSql {

    private final Logger log = Logger.getLogger(this.getClass());

    private static DynamicSql m_instance = null;

    private static HashMap<String, HashMap<String, String>> hscolumn = new HashMap<String, HashMap<String, String>>();

    private final static String SQLVALUE = "sqlvalue";

    private final static String SQLWHERE = "sqlwhere";

    private final static String SQLTABLENAME = "sqltablename";

    private final static String SQLTABLEPKNAME = "sqltablepkname";

    private static DozerBeanMapper beanMapper;

    public synchronized static DynamicSql getInstance() {

        if (m_instance == null) {

            m_instance = new DynamicSql();
            m_instance.init();
        }

        return m_instance;
    }

    private void init() {
        //        BeanFactory beanFactory = new ClassPathXmlApplicationContext(
        //            "classpath*:/config/**/application-*.xml");
        beanMapper = (DozerBeanMapper) SpringContextUtil.getBean("beanMapper");
    }

    public HashMap<String, Object> beanTohashmap(Object ob) throws IllegalArgumentException,
            IllegalAccessException {

        Field[] field = ob.getClass().getDeclaredFields();

        HashMap<String, Object> map = new HashMap<String, Object>();

        for (int i = 0; i < field.length; i++) {

            Field f = field[i];
            f.setAccessible(true);

            map.put(f.getName(), f.get(ob));

        }

        return map;

    }

    /**
     * 根据PK值修改表
     *
     * @param ob
     * @param tablename
     * @return
     */
    public HashMap<String, Object> UpdatebeanToSqlPk(Object ob, String tableName) {

        HashMap<String, Object> sql_hs = new HashMap<String, Object>();

        //是SET 的动态SQL
        ArrayList<KeyVO> setsql = new ArrayList<KeyVO>();
        //是WHERE的值
        ArrayList<KeyVO> wherepk = new ArrayList<KeyVO>();

        try {

            ObjectVOtoKeyVOListPK(ob, setsql, wherepk);

            tableName = getTableName(ob, tableName);

            sql_hs.put(DynamicSql.SQLTABLENAME, tableName);

            sql_hs.put(DynamicSql.SQLVALUE, setsql);

            sql_hs.put(DynamicSql.SQLWHERE, wherepk);

        } catch (IllegalArgumentException e) {
            log.error("::", e);
        } catch (IllegalAccessException e) {
            log.error("::", e);
        }

        return sql_hs;

    }

    /**
     * 根据传入的OB动态的生成DELETE中的SQL
     *
     * @param ob
     * @param tablename
     * @return
     */
    public HashMap<String, Object> DeletebeanToSql(Object ob, String tableName) {

        HashMap<String, Object> sql_hs = new HashMap<String, Object>();

        //是SET 的动态SQL
        ArrayList<KeyVO> setsql = new ArrayList<KeyVO>();

        try {

            ObjectVOtoKeyVOList(ob, setsql);

            tableName = getTableName(ob, tableName);

            sql_hs.put(DynamicSql.SQLTABLENAME, tableName);

            sql_hs.put(DynamicSql.SQLWHERE, setsql);

        } catch (IllegalArgumentException e) {
            log.error("::", e);
        } catch (IllegalAccessException e) {
            log.error("::", e);
        }

        return sql_hs;

    }

    /**
     * 根据传入的OB动态的生成SELECT中的SQL
     *
     * @param ob
     * @param tablename
     * @param change
     * @return
     */
    public HashMap<String, Object> SelectSql(Object ob, String tableName) {

        StringBuffer selectsql = new StringBuffer(" ");

        HashMap<String, Object> sql_hs = new HashMap<String, Object>();

        //是SET 的动态SQL
        ArrayList<KeyVO> wheresetsql = new ArrayList<KeyVO>();

        try {

            ObjectVOtoKeyVOListSelect(ob, wheresetsql, selectsql);

            tableName = getTableName(ob, tableName);

            sql_hs.put(DynamicSql.SQLTABLENAME, tableName);

            sql_hs.put(DynamicSql.SQLVALUE, selectsql.toString().substring(0,
                    selectsql.toString().lastIndexOf(",")));

            sql_hs.put(DynamicSql.SQLWHERE, wheresetsql);

        } catch (IllegalArgumentException e) {
            log.error("::", e);
        } catch (IllegalAccessException e) {
            log.error("::", e);
        }

        return sql_hs;

    }

    /**
     * 根据传入的OB动态的生成SELECTCOUNT中的SQL
     *
     * @param ob
     * @param tablename
     * @param change
     * @return
     */
    public HashMap<String, Object> SelectCountSql(Object ob, String tableName) {

        StringBuffer selectsql = new StringBuffer(" ");

        HashMap<String, Object> sql_hs = new HashMap<String, Object>();

        //是SET 的动态SQL
        ArrayList<KeyVO> wheresetsql = new ArrayList<KeyVO>();

        try {

            ObjectVOtoSelectCount(ob, wheresetsql);

            tableName = getTableName(ob, tableName);

            sql_hs.put(DynamicSql.SQLTABLENAME, tableName);

            sql_hs.put(DynamicSql.SQLWHERE, wheresetsql);

        } catch (IllegalArgumentException e) {
            log.error("::", e);
        } catch (IllegalAccessException e) {
            log.error("::", e);
        }

        return sql_hs;

    }

    public HashMap<String, Object> InsertSql(Object ob, String tableName, Object tablePk) {

        StringBuffer selectsql = new StringBuffer("( ");

        HashMap<String, Object> sql_hs = new HashMap<String, Object>();

        //是SET 的动态SQL
        ArrayList<KeyVO> wheresetsql = new ArrayList<KeyVO>();

        try {

            tableName = getTableName(ob, tableName);

            ObjectVOtoKeyVOListInsert(ob, wheresetsql, selectsql, tablePk);

            sql_hs.put(DynamicSql.SQLTABLENAME, tableName);

            sql_hs.put(DynamicSql.SQLVALUE, selectsql.toString().substring(0,
                    selectsql.toString().lastIndexOf(","))
                    + " ) ");

            sql_hs.put(DynamicSql.SQLWHERE, wheresetsql);

            log.debug("insert sql is :" + sql_hs);

        } catch (IllegalArgumentException e) {
            log.error("::", e);
        } catch (IllegalAccessException e) {
            log.error("::", e);
        }

        return sql_hs;

    }

    public List ListHashToListbean(List list, Object model) {

        List beanlist = new ArrayList();

        if (list != null) {

            try {

                for (int i = 0; i < list.size(); i++) {

                    Object ob = model.getClass().newInstance();

                    HashMap hs = (HashMap) list.get(i);

                    beanMapper.map(hs, ob);
                    // BeanUtils.copyProperties(ob, hs);

                    beanlist.add(ob);

                }

            } catch (IllegalAccessException e) {
                log.error("::", e);
            } catch (InstantiationException e) {
                log.error("::", e);
            }

        }

        return beanlist;

    }

    /**
     * 将VO变成KEYVOLIST,WHERE条件是PK
     *
     * @param ob
     * @param keyvolist
     * @param wherePk
     * @param tablehs
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void ObjectVOtoKeyVOListPK(Object ob, ArrayList<KeyVO> keyVoList,
                                       ArrayList<KeyVO> wherePk) throws IllegalArgumentException,
            IllegalAccessException {

        //        Field[] field = ob.getClass().getDeclaredFields();
        //        
        Field[] field = getFields(ob);

        KeyVO thisvo = null;

        for (int i = 0; i < field.length; i++) {

            Field f = field[i];

            f.setAccessible(true);

            if (f.getName().toUpperCase().equals("SERIALVERSIONUID")) {

                continue;

            }

            if (f.get(ob) == null) {

                continue;

            }

            String type = f.getType().getCanonicalName();

            if (!CleanoutType(type)) {

                continue;

            }

            String selectColumn = f.getName();

            thisvo = new KeyVO();

            thisvo.setKey(selectColumn.toUpperCase());

            thisvo.setValue(f.get(ob));

            if (f.getName().toUpperCase().equals(

                    IBatisDaoUtils.getPrimaryKeyFieldName((IBaseDto) ob).toUpperCase())) {

                if (wherePk != null) {

                    wherePk.add(thisvo);
                }

            } else {

                keyVoList.add(thisvo);

            }

            thisvo = null;

        }

    }

    /**
     * 将VO变成KEYVOLIST
     *
     * @param ob
     * @param keyvolist
     * @param tablehs
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void ObjectVOtoKeyVOList(Object ob, ArrayList<KeyVO> keyvolist)
            throws IllegalArgumentException,
            IllegalAccessException {

        Field[] field = ob.getClass().getDeclaredFields();

        KeyVO thisvo = null;

        for (int i = 0; i < field.length; i++) {

            Field f = field[i];

            f.setAccessible(true);

            if (f.getName().toUpperCase().equals("SERIALVERSIONUID")) {

                continue;

            }

            if (f.get(ob) == null) {

                continue;

            }

            String type = f.getType().getCanonicalName();

            if (!CleanoutType(type)) {

                continue;

            }

            String selectColumn = f.getName();

            thisvo = new KeyVO();

            thisvo.setKey(selectColumn.toUpperCase());

            thisvo.setValue(f.get(ob));

            keyvolist.add(thisvo);

            thisvo = null;

        }

    }

    /**
     * 生成SELECT的HASHMAP
     *
     * @param ob
     * @param keyvolist
     * @param tablehs
     * @param selectvalue
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void ObjectVOtoKeyVOListSelect(Object ob, ArrayList<KeyVO> whereKeyVoList,
                                           StringBuffer selectValue)
            throws IllegalArgumentException,
            IllegalAccessException {

        Field[] field = getFields(ob);

        KeyVO thisvo = null;

        for (int i = 0; i < field.length; i++) {

            Field f = field[i];

            f.setAccessible(true);

            if (f.getName().toUpperCase().equals("SERIALVERSIONUID")) {

                continue;

            }

            String selectColumn = f.getName();

            String type = f.getType().getCanonicalName();

            if (CleanoutType(type)) {

                selectValue.append(selectColumn + " \"" + selectColumn + "\" , ");

            } else {

                continue;
            }

            if (f.get(ob) == null) {

                continue;

            }

            thisvo = new KeyVO();

            thisvo.setKey(selectColumn.toUpperCase());

            thisvo.setValue(f.get(ob));

            whereKeyVoList.add(thisvo);

            thisvo = null;

        }

    }

    /**
     * 生成SELECTCount的HASHMAP
     *
     * @param ob
     * @param keyvolist
     * @param tablehs
     * @param selectvalue
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void ObjectVOtoSelectCount(Object ob, ArrayList<KeyVO> whereKeyVoList)
            throws IllegalArgumentException,
            IllegalAccessException {
        Field[] field = getFields(ob);

        KeyVO thisvo = null;

        for (int i = 0; i < field.length; i++) {

            Field f = field[i];

            f.setAccessible(true);

            if (f.getName().toUpperCase().equals("SERIALVERSIONUID")) {

                continue;

            }

            String selectColumn = f.getName();

            String type = f.getType().getCanonicalName();

            if (f.get(ob) == null) {

                continue;

            }

            thisvo = new KeyVO();

            thisvo.setKey(selectColumn.toUpperCase());

            thisvo.setValue(f.get(ob));

            whereKeyVoList.add(thisvo);

            thisvo = null;

        }

    }

    /**
     * 生成INSERT INTO的HASHMAP
     *
     * @param ob
     * @param keyvolist
     * @param tablehs
     * @param selectvalue
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void ObjectVOtoKeyVOListInsert(Object ob, ArrayList<KeyVO> keyvolist,
                                           StringBuffer selectvalue, Object tablePk)
            throws IllegalArgumentException,
            IllegalAccessException {

        Field[] field = getFields(ob);

        KeyVO thisvo = null;

        for (int i = 0; i < field.length; i++) {

            Field f = field[i];

            f.setAccessible(true);

            if (f.getName().toUpperCase().equals(
                    IBatisDaoUtils.getPrimaryKeyFieldName((IBaseDto) ob).toUpperCase())) {

                selectvalue.append(f.getName().toUpperCase() + ", ");

                thisvo = new KeyVO();

                thisvo.setKey(f.getName().toUpperCase());

                thisvo.setValue(tablePk);

                keyvolist.add(thisvo);

                continue;

            }

            if (f.get(ob) == null) {

                continue;

            }

            if (f.getName().toUpperCase().equals("SERIALVERSIONUID")) {

                continue;

            }

            String selectColumn = f.getName();

            String type = f.getType().getCanonicalName();

            if (CleanoutType(type)) {

                selectvalue.append(selectColumn.toUpperCase() + ", ");

            } else {

                continue;
            }

            thisvo = new KeyVO();

            thisvo.setKey(selectColumn.toUpperCase());

            thisvo.setValue(f.get(ob));

            keyvolist.add(thisvo);

            thisvo = null;

        }

    }

    /**
     * 根据PK值逻辑删除数据
     *
     * @param ob
     * @param tablename
     * @return
     */
    public HashMap<String, Object> UpdateBatchbeanToSqlPk(Object ob, List<Long> wherepk,
                                                          String tableName) {

        HashMap<String, Object> sql_hs = new HashMap<String, Object>();

        //是SET 的动态SQL
        ArrayList<KeyVO> setsql = new ArrayList<KeyVO>();

        try {

            ObjectVOtoKeyVOListPK(ob, setsql, null);

            tableName = getTableName(ob, tableName);

            sql_hs.put(DynamicSql.SQLTABLENAME, tableName);

            sql_hs.put(DynamicSql.SQLVALUE, setsql);

            sql_hs.put(DynamicSql.SQLWHERE, wherepk);

            sql_hs.put(DynamicSql.SQLTABLEPKNAME, IBatisDaoUtils.getPrimaryKeyFieldName(
                    (IBaseDto) ob).toUpperCase());

        } catch (IllegalArgumentException e) {
            log.error("::", e);
        } catch (IllegalAccessException e) {
            log.error("::", e);
        }

        return sql_hs;

    }

    private boolean CleanoutType(String type) {

        if (type.equals("java.lang.Long") || type.equals("java.lang.String")
                || type.equals("java.lang.Integer") || type.equals("java.lang.Double")
                || type.equals("java.sql.Timestamp") || type.equals("java.math.BigDecimal")
                || type.equals("java.util.Date") || type.equals("java.sql.Date")) {

            return true;

        }

        return false;

    }

    private String getTableName(Object ob, String tableName) {

        return IBatisDaoUtils.getTableName(ob, tableName);

    }

    private Field[] getFields(Object ob) {

        Field[] field = ob.getClass().getDeclaredFields();

        if (IBatisDaoUtils.getAddParentClass(ob)) {

            Field[] superFields = BaseDtoImpl.class.getDeclaredFields();

            Field[] field_all = new Field[superFields.length + field.length];

            int supernum = superFields.length;

            for (int i = 0; i < supernum; i++) {

                field_all[i] = superFields[i];

            }

            for (int i = supernum; i < field.length + supernum; i++) {

                field_all[i] = field[i - supernum];

            }

            return field_all;

        }

        return field;

    }

}
