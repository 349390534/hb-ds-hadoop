package com.howbuy.hadoop.hbase.orm.schema;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.howbuy.hadoop.hbase.orm.annotation.HColumn;
import com.howbuy.hadoop.hbase.orm.annotation.HTable;
import com.howbuy.hadoop.hbase.orm.exceptions.HOrmException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * This is factory of DataMapper, each Type can has one DataMapperFactory and
 * the factory can create DataMapper according to the instance
 *
 * @author Administrator
 *
 * @param <T>
 */
public class DataMapperFactory<T> {
	Log LOG = LogFactory.getLog(DataMapperFactory.class);
	public String tablename;
	// for schema
	public Map<Field, FamilyQualifierSchema> fixedSchema;
	// for data type
	public Map<Field, FieldDataType> fieldDataType;
	public Field rowkeyField;
	public Class<?> dataClass;

	public DataMapperFactory(Class<T> dataClass_) throws HOrmException {
		dataClass = dataClass_;
		// set tableName
		setTableName();
		// set fixed schema
		fixedSchema = new HashMap<Field, FamilyQualifierSchema>();
		fieldDataType = new HashMap<Field, FieldDataType>();
		setFixedSchemaAndDataType();
	}

	public DataMapper<T> create(T instance) throws HOrmException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		// check type
		if (!instance.getClass().equals(dataClass)) {
			return null;
		}
		// a new DataMapper constructed with the fixed members
		DataMapper<T> dm = new DataMapper<T>(tablename, fixedSchema,
				fieldDataType, rowkeyField, dataClass);
		// 1. copy the fixed schema to datafieldToSchema. </br>
		// 2. fill value according to ... to Value of datafieldToSchema; </br>
		// notice:
		dm.copyToDataFieldSchemaFromFixedSchema();
		dm.copyToDataFieldsFromInstance(instance);
		return dm;
	}

	/**
	 * create an empty DataMapper for the instance, uses can further:<br>
	 * <code>
	 * <li>setRowKey</li><br>
	 * <li>setFieldValue(FieldName, Object)</li><br>
	 * <li>setFieldValue(FieldName, SubFieldName, Object)</li><br>
	 * </code>
	 *
	 * @param instance  .
	 * @return          .
	 * @throws com.howbuy.hadoop.hbase.orm.exceptions.HOrmException
	 */
	public DataMapper<T> createEmpty(T instance) throws HOrmException {
		// check type
		if (!instance.getClass().equals(dataClass)) {
			return null;
		}
		// a new DataMapper constructed with the fixed members
		DataMapper<T> dm = new DataMapper<T>(tablename, fixedSchema,
				fieldDataType, rowkeyField, dataClass);

		dm.copyToDataFieldSchemaFromFixedSchema();

		return dm;
	}

	public DataMapper<T> createEmpty(Class<?> clazz) throws HOrmException {
		// check type
		if (!clazz.equals(dataClass)) {
			return null;
		}
		// a new DataMapper constructed with the fixed members
		DataMapper<T> dm = new DataMapper<T>(tablename, fixedSchema,
				fieldDataType, rowkeyField, dataClass);

		dm.copyToDataFieldSchemaFromFixedSchema();

		return dm;
	}

	/**
	 * a helper method to return script to create the HBase table according to
	 * fixedSchema
	 *
	 * @return Script to create create the table
	 */
	public String tableCreateScript() {
		StringBuilder sb = new StringBuilder();
		sb.append("create '");
		sb.append(tablename).append("', ");
		for (Field field : fixedSchema.keySet()) {
			FamilyQualifierSchema sc = fixedSchema.get(field);
			String family = Bytes.toString(sc.getFamily());
			sb.append("{NAME => '").append(family).append("'},");
		}

		return sb.toString().substring(0, sb.length() - 1);
	}

	public HTableDescriptor tableCreateDescriptor() {

		HTableDescriptor td = new HTableDescriptor(Bytes.toBytes(tablename));
		for (Field field : fixedSchema.keySet()) {
			FamilyQualifierSchema sc = fixedSchema.get(field);
			td.addFamily(new HColumnDescriptor(sc.getFamily()));
		}

		return td;

	}

	/**
	 * if annotation is not set, use class name instead
	 */
	private void setTableName() {
		HTable hTable = (HTable) dataClass
				.getAnnotation(HTable.class);
		if (hTable == null || hTable.tableName().length() == 0) {
			LOG.warn("Table name is not specified as annotation, use class name instead");
			tablename = dataClass.getSimpleName();
		} else {
			tablename = hTable.tableName();
		}
	}
	
	
	/**
	 * 覆盖annotation获取的table
	 * @param tablename
	 */
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	/**
	 * <li>if annotation for a field is not set, the field is omitted. <br> <li>
	 * if is a rowkey; <br> <li>
	 * others
	 *
	 * @throws com.howbuy.hadoop.hbase.orm.exceptions.HOrmException
	 */
	private void setFixedSchemaAndDataType() throws HOrmException {
		// TODO: maybe need to deal with inheritance scenario: dataClass's
		// super-class
		for (Field field : dataClass.getDeclaredFields()) {
			HColumn hColumn = field
					.getAnnotation(HColumn.class);
			if (hColumn == null) {
				// not included in base
				fieldDataType.put(field, new FieldDataType(FieldDataType.SKIP,
						null));
				continue;
			}
			if (hColumn.id()) {
				// set the field as id
				rowkeyField = field;
				continue;
			}

			FamilyQualifierSchema fqv = fQSchemaAndDataTypeBuildFromField(
                    hColumn, field);

			fixedSchema.put(field, fqv);
		}
	}

	/**
	 * Set family, qualifier schema according to the field's annotation. <b>Side
	 * effect:</b> set field data type for each field and also for fields of
	 * class as family class (sub level class)
	 *
	 * @param hColumn  .
	 * @param field          .
	 * @return    .
	 * @throws com.howbuy.hadoop.hbase.orm.exceptions.HOrmException
	 */
	private FamilyQualifierSchema fQSchemaAndDataTypeBuildFromField(
            HColumn hColumn, Field field) throws HOrmException {

		String family;
		String qualifier;
		Map<String, byte[]> subFieldToQualifier = null;
		// TODO
		// 1. primitive type or string
		if (field.getType().isPrimitive()
				|| field.getType().equals(String.class)) {
			if (hColumn.familyName().length() == 0) {
				throw new HOrmException(
						"For primitive typed field "
								+ dataClass.getName()
								+ "."
								+ field.getName()
								+ " we must define family with annotation: familyName=\"familyname\".");
			} else {
				family = getDatabaseColumnName(hColumn.familyName(),
						field);
				qualifier = getDatabaseColumnName(
						hColumn.qualifierName(), field);
				fieldDataType.put(field, new FieldDataType(
						FieldDataType.PRIMITIVE, field.getType()));
			}
		} else if (field.getType().equals(List.class)
				|| hColumn.isQualifierList()) {
			// only set family, qualifier is ...
			family = getDatabaseColumnName(hColumn.familyName(), field);
			qualifier = null;
			if (hColumn.isQualifierList()) {
				LOG.warn("Field "
						+ field.getName()
						+ " is not 'List' (maybe a ArrayList??) but set as qualifierList. May be wrong when converted to 'List' ...");
			}
			fieldDataType.put(field, new FieldDataType(FieldDataType.LIST,
					field.getType()));
		}
		// Map,,
		else if (field.getType().equals(Map.class)
				|| hColumn.isQualifierValueMap()) {
			// only set family, qualifier is ...
			family = getDatabaseColumnName(hColumn.familyName(), field);
			qualifier = null;
			if (hColumn.isQualifierValueMap()) {
				LOG.warn("Field "
						+ field.getName()
						+ " is not 'Map' (maybe a HashMap??) but set as qualifierValueMap. May be wrong when converted to 'Map' ...");
			}
			fieldDataType.put(field,
					new FieldDataType(FieldDataType.MAP, field.getType()));
		}
		// others
		else {
			// non-primitive and not List
			family = getDatabaseColumnName(hColumn.familyName(), field);
			qualifier = null;

			// check whether is a sub level class as family
			HTable subHTable = (HTable) field.getType()
					.getAnnotation(HTable.class);
			if (subHTable != null && subHTable.canBeFamily()) {
				// create a FieldDataType and later
				FieldDataType fieldDataTypeForFamilyClass = new FieldDataType(
						FieldDataType.SUBLEVELFAMCLASS, field.getType());
				fieldDataType.put(field, fieldDataTypeForFamilyClass);

				for (Field subField : field.getType().getDeclaredFields()) {
					HColumn subHColumn = subField
							.getAnnotation(HColumn.class);
					if (subHColumn == null) {
						fieldDataTypeForFamilyClass.addSubLevelDataType(
                                subField, new FieldDataType(FieldDataType.SKIP,
										null));
						continue;
					}
					byte[] subQualifierName;
					// 2012-7-12, wlu: if is list or map, skip this
					if (subHColumn.isQualifierList()) {
						fieldDataTypeForFamilyClass.addSubLevelDataType(
                                subField, new FieldDataType(FieldDataType.LIST,
										subField.getType()));
						continue;
					} else if (subHColumn.isQualifierValueMap()) {
						fieldDataTypeForFamilyClass.addSubLevelDataType(
                                subField, new FieldDataType(FieldDataType.MAP,
										subField.getType()));
						continue;
					}
					// field name as qualifier name. Whatever primitive or
					// String or UDF class, we treat is as PRIMITIVE
					else {
						fieldDataTypeForFamilyClass.addSubLevelDataType(
                                subField,
								new FieldDataType(FieldDataType.PRIMITIVE,
										subField.getType()));
						subQualifierName = Bytes.toBytes(getDatabaseColumnName(
								subHColumn.qualifierName(), subField));
					}
					// initial once
					if (subFieldToQualifier == null) {
						subFieldToQualifier = new HashMap<String, byte[]>();
					}
					subFieldToQualifier.put(subField.getName(),
							subQualifierName);
				}
			}
		}

		// TODO
		FamilyQualifierSchema fqv = new FamilyQualifierSchema();

		fqv.setFamily(Bytes.toBytes(family));
		if (qualifier == null) {
			fqv.setQualifier(null);
		} else {
			fqv.setQualifier(Bytes.toBytes(qualifier));
		}
		fqv.setSubFieldToQualifier(subFieldToQualifier);

		return fqv;

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

//    public static void main(String[] args) {
//        try {
//            DataMapperFactory dataMapperFactory = new DataMapperFactory(UserInfo.class);
//        } catch (HOrmException e) {
//            e.printStackTrace();
//        }
//    }

}
