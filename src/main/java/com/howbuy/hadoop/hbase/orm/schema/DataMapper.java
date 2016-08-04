package com.howbuy.hadoop.hbase.orm.schema;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import com.howbuy.hadoop.hbase.orm.annotation.HColumn;
import com.howbuy.hadoop.hbase.orm.connection.HConnection;
import com.howbuy.hadoop.hbase.orm.exceptions.HOrmException;
import com.howbuy.hadoop.hbase.orm.schema.value.NullValue;
import com.howbuy.hadoop.hbase.orm.schema.value.StringValue;
import com.howbuy.hadoop.hbase.orm.schema.value.Value;
import com.howbuy.hadoop.hbase.orm.schema.value.ValueFactory;
import com.howbuy.hadoop.hbase.orm.util.Utils;

/**
 * Each
 * 
 * @author Administrator
 * 
 * @param <T>
 */
public class DataMapper<T> {
	Log LOG = LogFactory.getLog(DataMapper.class);
	// fixed schema for the generic type T, copy from the factory
	public String tableName;
	public Map<Field, FamilyQualifierSchema> fixedSchema;
	public Map<Field, FieldDataType> fieldDataType;
	public Field rowKeyField;
	public Class<?> dataClass;

	// private data for individual instance
	private Map<Field, familyToQualifersAndValues> dataFieldsToFamilyQualifierValue;
	// private data for rowKey
	private Value rowKey;

	/**
	 * Construct with fixed members as parameters
	 * 
	 * @param tableName        .
	 * @param fixedSchema       .
	 * @param rowKeyField      .
	 * @param dataClass         .
	 */
	public DataMapper(String tableName,
			Map<Field, FamilyQualifierSchema> fixedSchema,
			Map<Field, FieldDataType> fieldDataType, Field rowKeyField,
			Class<?> dataClass) {
		this.tableName = tableName;
		this.fieldDataType = fieldDataType;
		this.fixedSchema = fixedSchema;
		this.rowKeyField = rowKeyField;
		this.dataClass = dataClass;
	}

	// insert the instance to HBase
	public void insert(HConnection connection) {
		Put put = new Put(rowKey.toBytes());
		// add each field's data to the 'put'
		for (Field field : dataFieldsToFamilyQualifierValue.keySet()) {
			dataFieldsToFamilyQualifierValue.get(field).addToPut(put);
		}

		connection.insert(Bytes.toBytes(tableName), put);
	}

	public T queryById(Value id, HConnection connection) {
		byte[] rowkey = id.toBytes();
		Get get = new Get(rowkey);

		try {
            Result result = connection.query(Bytes.toBytes(tableName), get);
			return createObjectFromResult(result);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}

	private T createObjectFromResult(Result result) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Class<?> clazz = dataClass;
		Constructor<?> constr = clazz.getDeclaredConstructor();
		Object instance = constr.newInstance();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.equals(rowKeyField)) {
				byte[] value = result.getRow();
				Object fieldInstance = ValueFactory.createObject(
                        field.getType(), value);
				Utils.setToField(instance, field, fieldInstance);
				continue;
			}
			// data type info
			FieldDataType fdt = fieldDataType.get(field);
			// schema info
			FamilyQualifierSchema fqs = fixedSchema.get(field);
			if (fdt.isSkip()) {
				continue;
			} else if (fdt.isPrimitive()) {
				byte[] value = result.getValue(fqs.getFamily(),
						fqs.getQualifier());
				Class<?> fieldClazz = fdt.dataclass;
				// convert from byte[] to Object according to field clazz
				Object fieldInstance = ValueFactory.createObject(fieldClazz,
                        value);
				Utils.setToField(instance, field, fieldInstance);
			} else if (fdt.isList()) {
				// get qualifier names and add the the list
				NavigableMap<byte[], byte[]> qvmap = result.getFamilyMap(fqs
						.getFamily());
				List<String> lst = new ArrayList<String>();
				for (byte[] q : qvmap.keySet()) {
					lst.add(Bytes.toString(q));
				}
				Utils.setToField(instance, field, lst);
			} else if (fdt.isMap()) {
				// get qualifier-value map and put the map
				NavigableMap<byte[], byte[]> qvmap = result.getFamilyMap(fqs
						.getFamily());
				Map<String, String> map2 = new HashMap<String, String>();
				for (byte[] q : qvmap.keySet()) {
					map2.put(Bytes.toString(q), Bytes.toString(qvmap.get(q)));
				}
				Utils.setToField(instance, field, map2);
			} else if (fdt.isSubLevelClass()) {
				// get the qualifer-object....
				Object sublevelObj = createSubLevelObject(
                        fqs.getSubFieldToQualifier(), fdt,
                        result.getFamilyMap(fqs.getFamily()));
				Utils.setToField(instance, field, sublevelObj);
			}
		}

		@SuppressWarnings("unchecked")
		T RetObject = (T) instance;

		return RetObject;
	}

	private Object createSubLevelObject(
            Map<String, byte[]> subfieldToQualifier, FieldDataType fdt,
            NavigableMap<byte[], byte[]> map) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Class<?> fieldClazz = fdt.dataclass;
		Constructor<?> constr = fieldClazz.getDeclaredConstructor();
		Object fieldInstance = constr.newInstance();

		for (Field subField : fieldClazz.getDeclaredFields()) {
			FieldDataType subDataType = fdt.getSubLevelDataType(subField);
			String fieldStringName = subField.getName();
			if (subDataType.isSkip()) {
				continue;
			} else if (subDataType.isPrimitive()) {
				byte[] value = map
						.get(subfieldToQualifier.get(fieldStringName));

				Class<?> subFieldClazz = subDataType.dataclass;
				// convert from byte[] to Object according to field clazz
				Object subFieldInstance = ValueFactory.createObject(
                        subFieldClazz, value);
				Utils.setToField(fieldInstance, subField, subFieldInstance);
			} else if (subDataType.isList()) {
				NavigableMap<byte[], byte[]> qvmap = map;
				List<String> lst = new ArrayList<String>();
				for (byte[] q : qvmap.keySet()) {
					lst.add(Bytes.toString(q));
				}
				Utils.setToField(fieldInstance, subField, lst);
			} else if (subDataType.isMap()) {
				NavigableMap<byte[], byte[]> qvmap = map;
				Map<String, String> map2 = new HashMap<String, String>();
				for (byte[] q : qvmap.keySet()) {
					map2.put(Bytes.toString(q), Bytes.toString(qvmap.get(q)));
				}
				Utils.setToField(fieldInstance, subField, map2);
			} else {
				Utils.setToField(fieldInstance, subField, null);
			}
		}
		return fieldInstance;
	}

	/**
	 * Copy from the fixed schema. All members used in the method are fixed
	 * according to the <code>dataClass</code>
	 * 
	 * @throws com.howbuy.hadoop.hbase.orm.exceptions.HOrmException
	 */
	public void copyToDataFieldSchemaFromFixedSchema() throws HOrmException {
		dataFieldsToFamilyQualifierValue = new HashMap<Field, familyToQualifersAndValues>();
		for (Field field : fixedSchema.keySet()) {
			FamilyQualifierSchema fqv = fixedSchema.get(field);
			if (fqv.getFamily() == null) {
				throw new HOrmException("Family should not be null!");
			}
			// if(fqv.getQualifier()== null){
			familyToQualifersAndValues f2qvs = new familyToQualifersAndValues();
			f2qvs.setFamily(fqv.getFamily());
			dataFieldsToFamilyQualifierValue.put(field, f2qvs);
			// }

		}
	}

	public Map<Field, familyToQualifersAndValues> getDataFieldsToFamilyQualifierValue() {
		return dataFieldsToFamilyQualifierValue;
	}

	public void setDataFieldsToFamilyQualifierValue(
            Map<Field, familyToQualifersAndValues> dataFieldsToFamilyQualifierValue) {
		this.dataFieldsToFamilyQualifierValue = dataFieldsToFamilyQualifierValue;
	}

	/**
	 * create a concret DataMapper instance by filling rowKey, family:qualifier
	 * etc
	 * 
	 * @param instance
	 * @throws IllegalArgumentException
	 * @throws com.howbuy.hadoop.hbase.orm.exceptions.HOrmException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void copyToDataFieldsFromInstance(T instance)
			throws IllegalArgumentException, HOrmException,
			IllegalAccessException, InvocationTargetException {
		for (Field field : instance.getClass().getDeclaredFields()) {
			// if is rowKey
			if (rowKeyField.equals(field)) {
				rowKey = ValueFactory
						.create(Utils.getFromField(instance, field));
				continue;
			}
			FamilyQualifierSchema fq = fixedSchema.get(field);
			FieldDataType fdt = fieldDataType.get(field);
			// field not included for HBase
			if (fq == null) {
				continue;
			}

			// Primitive, family and qualifier name are both specified
			if (fq.getQualifier() != null) {
				Value value = ValueFactory.create(Utils.getFromField(instance,
                        field));
				if(value!=null){
					if(value instanceof StringValue){
						StringValue stringValue = (StringValue)value;
						String strValue = stringValue.getStringValue();
						if(!"".equals(strValue)&&!"null".equalsIgnoreCase(strValue)){
							dataFieldsToFamilyQualifierValue.get(field).add( fq.getQualifier(), value);
							LOG.debug("dataFieldsToFamilyQualifierValue add : field="+field.getName()+", value="+strValue);
						}
					}else if (value instanceof NullValue){
						// nothing to do
					}else{
						dataFieldsToFamilyQualifierValue.get(field).add( fq.getQualifier(), value);
						LOG.debug("dataFieldsToFamilyQualifierValue add : field="+field.getName()+", value="+value.toString());
					}
				}
			} else {
				// user defined class or a list as family data <br/>
				// 1. user defined class, need to add fixed qualifer informtion
				// to the fixedField
				if (fdt.isSubLevelClass()/* databasetable.canBeFamily() */) {
					Map<byte[], Value> qualifierValues = getQualifierValuesFromInstanceAsFamily(
                            Utils.getFromField(instance, field), fq, fdt);
					dataFieldsToFamilyQualifierValue.get(field).add(
                            qualifierValues);
				} else if (fdt.isList()/* databasefield.isQualifierList() */) {
					@SuppressWarnings("unchecked")
					List<String> list = (List<String>) (Utils.getFromField(
                            instance, field));

					if (list == null) {
						continue;
					}
					for (String key : list) {
						String qualifier = key;
						Value value = ValueFactory.create(null);

						dataFieldsToFamilyQualifierValue.get(field).add(
                                Bytes.toBytes(qualifier), value);
					}
				} else if (fdt.isMap()) {
					// 2. Map
					// TODO
				}

			}
		}
	}

	/**
	 * Just set the rowKey for the instance
	 * 
	 * @param instance
	 */
	public void setRowKey(T instance) {
		for (Field field : instance.getClass().getDeclaredFields()) {
			// if is rowKey
			if (rowKeyField.equals(field)) {
				try {
					rowKey = ValueFactory.create(Utils.getFromField(instance,
                            field));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}

	public void setFieldValue(T instance, List<String> fieldName)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, HOrmException {
		for (Field field : instance.getClass().getDeclaredFields()) {
			if (!fieldName.contains(field.getName())) {
				continue;
			}
			// if is rowKey
			if (rowKeyField.equals(field)) {
				rowKey = ValueFactory
						.create(Utils.getFromField(instance, field));
				continue;
			}
			FamilyQualifierSchema fq = fixedSchema.get(field);
			FieldDataType fdt = fieldDataType.get(field);
			// field not included for HBase
			if (fq == null) {
				continue;
			}

			// Primitive, family and qualifier name are both specified
			if (fq.getQualifier() != null) {
				Value value = ValueFactory.create(Utils.getFromField(instance,
                        field));
				dataFieldsToFamilyQualifierValue.get(field).add(
                        fq.getQualifier(), value);
			} else {
				// user defined class or a list as family data <br/>
				// 1. user defined class, need to add fixed qualifer informtion
				// to the fixedField
				if (fdt.isSubLevelClass()/* databasetable.canBeFamily() */) {
					Map<byte[], Value> qualifierValues = getQualifierValuesFromInstanceAsFamily(
                            Utils.getFromField(instance, field), fq, fdt);
					dataFieldsToFamilyQualifierValue.get(field).add(
                            qualifierValues);
				} else if (fdt.isList()/* databasefield.isQualifierList() */) {
					@SuppressWarnings("unchecked")
					List<String> list = (List<String>) (Utils.getFromField(
                            instance, field));

					if (list == null) {
						continue;
					}
					for (String key : list) {
						String qualifier = key;
						Value value = ValueFactory.create(null);

						dataFieldsToFamilyQualifierValue.get(field).add(
                                Bytes.toBytes(qualifier), value);
					}
				} else if (fdt.isMap()) {
					// 2. Map
					// TODO
				}

			}
		}

	}

	public void setFieldValue(T instance, String fieldName, String subFieldName) {

	}

	/**
	 * 
	 */
	// public void SetA

	/**
	 * Build a map {qualifier: value} from the object as family
	 * 
	 * @param instance
	 *            the object as family
	 * @return
	 * @throws com.howbuy.hadoop.hbase.orm.exceptions.HOrmException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Map<byte[], Value> getQualifierValuesFromInstanceAsFamily(
            Object instance, FamilyQualifierSchema fqs, FieldDataType fdt)
			throws HOrmException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		if (instance == null) {
			return null;
		}

		Map<byte[], Value> qualifierValues = new HashMap<byte[], Value>();
		{
			for (Field field : instance.getClass().getDeclaredFields()) {
				HColumn hColumn = field
						.getAnnotation(HColumn.class);
				if (fdt.isSkip()) {
					// not included in base
					continue;
				}
				Class<?> fieldType = field.getType();
				// 1. primitive type (actually include those UDF class, to which
				// we treat them as toString())
				if (fdt.getSubLevelDataType(field).isPrimitive()/*
																 * fieldType.
																 * isPrimitive()
																 */) {
					if (!fieldType.isPrimitive()) {
						LOG.warn("This is not good: instance is not primitive nor List nor Map , but "
								+ fieldType + ". We use toString() as value.");
					}
					String qualifier = getDatabaseColumnName(
							hColumn.qualifierName(), field);
					Value value = ValueFactory.create(Utils.getFromField(
                            instance, field));
					qualifierValues.put(Bytes.toBytes(qualifier), value);

				}
				// Map, maybe HashMap or other map, all converted to Map
				else if (fdt.getSubLevelDataType(field).isMap()) {
					// get each key as qualifier and value as value
					@SuppressWarnings("unchecked")
					Map<String, Object> map = (Map<String, Object>) Utils
							.getFromField(instance, field);
					for (String key : map.keySet()) {
						String qualifier = key;
						Value value = ValueFactory.create(map.get(key));
						qualifierValues.put(Bytes.toBytes(qualifier), value);
					}

				}
				// List, maybe ArrayList or others list, all converted to List
				else if (fdt.getSubLevelDataType(field).isList()) {
					// not good ...
					@SuppressWarnings("unchecked")
					List<String> list = (List<String>) (Utils.getFromField(
                            instance, field));
					for (String key : list) {
						String qualifier = key;
						Value value = ValueFactory.create(null);
						qualifierValues.put(Bytes.toBytes(qualifier), value);
					}
				} else {
					//
				}

			}
		}
		return qualifierValues;
	}

	private String getDatabaseColumnName(String string, Field field) {
		if (string.length() == 0) {
			LOG.info("Field "
					+ dataClass.getName()
					+ "."
					+ field.getName()
					+ " need to take care of ... field name is used as column name");
			return field.getName();
		}
		return string;
	}

}
