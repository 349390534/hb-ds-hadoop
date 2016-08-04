package com.howbuy.hadoop.hbase.orm.dao;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.howbuy.hadoop.hbase.orm.exceptions.HOrmException;
import com.howbuy.hadoop.hbase.orm.schema.DataMapperFactory;
import com.howbuy.hadoop.hbase.orm.util.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.Bytes;

import com.howbuy.hadoop.hbase.orm.connection.HConnection;
import com.howbuy.hadoop.hbase.orm.schema.DataMapper;
import com.howbuy.hadoop.hbase.orm.schema.value.Value;
import com.howbuy.hadoop.hbase.orm.schema.value.ValueFactory;

public class HDaoImpl<T> implements HDao<T> {

    private Log LOG = LogFactory.getLog(this.getClass());
    Class<T> dataClass;
    private HConnection hConnection;
    // set constant schemas
    private DataMapperFactory<T> dataMapperFactory = null;

    public HDaoImpl(){

    }

    public HDaoImpl(Class<T> dataClass, HConnection connection)
            throws HOrmException {
        this.dataClass = dataClass;
        hConnection = connection;
        dataMapperFactory = new DataMapperFactory<T>(dataClass);
    }

    public void initDao(Class<T> dataClass, HConnection connection) throws HOrmException {
        this.dataClass = dataClass;
        hConnection = connection;
        dataMapperFactory = new DataMapperFactory<T>(dataClass);
    }
    
    /**
     * 使用指定的tableName
     * @param dataClass
     * @param connection
     * @param tableName
     * @throws HOrmException
     */
    public void initDaoWithTableName(Class<T> dataClass, HConnection connection,String tableName) throws HOrmException {
        this.dataClass = dataClass;
        hConnection = connection;
        dataMapperFactory = new DataMapperFactory<T>(dataClass);
        dataMapperFactory.setTablename(tableName);
    }

    @Override
    public void createTable() {
        if (hConnection.tableExists(dataMapperFactory.tablename)) {
            hConnection.deleteTable(dataMapperFactory.tablename);
        }
        hConnection.createTable(dataMapperFactory.tableCreateDescriptor());
        LOG.info(dataMapperFactory.tableCreateScript());
    }

    @Override
    public void createTableIfNotExist() {
        if (hConnection.tableExists(dataMapperFactory.tablename)) {
            LOG.info("The table has already existed, will not recreate it.");
            return;
        }
        hConnection.createTable(dataMapperFactory.tableCreateDescriptor());
        LOG.info(dataMapperFactory.tableCreateScript());
    }

    @Override
    public void insert(T data) {
        // need to check the type
        if (!data.getClass().equals(dataClass)) {
            LOG.error("Class type of data is not the same as that of the HDao, should be "
                    + dataClass);
            return;
        }
        DataMapper<T> dataMapper = null;
        try {
            dataMapper = dataMapperFactory.create(data);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (HOrmException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        dataMapper.insert(hConnection);
    }

    @Override
    public void deleteById(Value rowkey) {
        org.apache.hadoop.hbase.client.Delete delete = new org.apache.hadoop.hbase.client.Delete(
                rowkey.toBytes());
        try {
            hConnection.delete(Bytes.toBytes(dataMapperFactory.tablename),
                    delete);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteById(T data) {
        try {
            Value rowkey = ValueFactory.create(Utils.getFromField(data,
                    dataMapperFactory.rowkeyField));
            deleteById(rowkey);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    /**
     * The qualifier is pretty complicated
     */
    public void delete(T data, String FieldNameOfFamily,
                       String fieldNameOfQualifier) {
        if (fieldNameOfQualifier == null) {
            delete(data, FieldNameOfFamily);
            return;
        }
        Value rowkey;
        try {

            rowkey = ValueFactory.create(Utils.getFromField(data,
                    dataMapperFactory.rowkeyField));
            org.apache.hadoop.hbase.client.Delete delete = new org.apache.hadoop.hbase.client.Delete(
                    rowkey.toBytes());
            // get family name
            Field familyNameField = data.getClass().getDeclaredField(
                    FieldNameOfFamily);
            byte[] familyName = getFamilyByFieldName(familyNameField,
                    FieldNameOfFamily);
            // get qualifier name
            byte[] qualifierName = getQualiferByFamilyOrSublevelFieldName(
                    familyNameField, fieldNameOfQualifier);

            delete.deleteColumn(familyName, qualifierName);
            hConnection.delete(Bytes.toBytes(dataMapperFactory.tablename),
                    delete);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (HOrmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(T data, String familyFieldName) {
        if (familyFieldName == null) {
            delete(data);
            return;
        }
        Value rowkey;
        try {
            rowkey = ValueFactory.create(Utils.getFromField(data,
                    dataMapperFactory.rowkeyField));
            org.apache.hadoop.hbase.client.Delete delete = new org.apache.hadoop.hbase.client.Delete(
                    rowkey.toBytes());
            // get family name
            Field familyNameField = data.getClass().getDeclaredField(
                    familyFieldName);
            byte[] familyName = getFamilyByFieldName(familyNameField,
                    familyFieldName);
            delete.deleteFamily(familyName);
            hConnection.delete(Bytes.toBytes(dataMapperFactory.tablename),
                    delete);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private byte[] getFamilyByFieldName(Field familyNameField,
                                        String familyFieldName) throws SecurityException,
            NoSuchFieldException {
        byte[] familyName = dataMapperFactory.fixedSchema.get(familyNameField)
                .getFamily();
        return familyName;
    }

    private byte[] getQualiferByFamilyOrSublevelFieldName(
            Field familyNameField, String FieldNameOfQualifier)
            throws HOrmException {
        // if qualifier name is set with family name
        byte[] qualifierName = dataMapperFactory.fixedSchema.get(
                familyNameField).getQualifier();
        // qualifier is not directly set or set with a wrong value
        if (qualifierName == null
                || Bytes.compareTo(qualifierName,
                Bytes.toBytes(FieldNameOfQualifier)) != 0) {
            qualifierName = null;
        }
        if (qualifierName == null) {
            Map<String, byte[]> subFieldToQualifier = dataMapperFactory.fixedSchema
                    .get(familyNameField).getSubFieldToQualifier();
            if (subFieldToQualifier == null) {
                qualifierName = null;
            } else if (subFieldToQualifier.get(FieldNameOfQualifier) != null) {
                qualifierName = subFieldToQualifier.get(FieldNameOfQualifier);
            } else {
                throw new HOrmException("The field '"
                        + FieldNameOfQualifier
                        + "' of sub level family class '"
                        + familyNameField.getName()
                        + "' is not set as qualifier");
            }
            // else qualifier is set with name of the field's name
            if (qualifierName == null) {
                qualifierName = Bytes.toBytes(FieldNameOfQualifier);
            }
        }
        return qualifierName;
    }

    @Override
    public void delete(T data) {
        deleteById(data);

    }

    @Override
    public void update(T data) {
        insert(data);

    }

    @Override
    public void update(T data, List<String> familyFieldName) {
        if (familyFieldName == null) {
            update(data);
            return;
        }
        try {
            DataMapper<T> dm = dataMapperFactory.createEmpty(data);
            dm.setRowKey(data);
            dm.setFieldValue(data, familyFieldName);
            dm.insert(hConnection);
        } catch (HOrmException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public T queryById(Value id) {
        DataMapper<T> dm = null;
        try {
            dm = dataMapperFactory.createEmpty(dataClass);
        } catch (HOrmException e) {
            e.printStackTrace();
        }
        if (dm == null) {
            return null;
        }
        return dm.queryById(id, hConnection);
    }

    @Override
    public List<T> queryWithFilter(String filter, boolean returnWholeObject) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<T> queryWithFilter(String filter) {
        // TODO Auto-generated method stub
        return null;
    }

}
