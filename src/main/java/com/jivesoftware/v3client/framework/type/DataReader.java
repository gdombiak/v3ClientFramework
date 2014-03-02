package com.jivesoftware.v3client.framework.type;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.*;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.*;

/**
 * Created by ed.venaglia on 3/1/14.
 */
public class DataReader {

    public static final ThreadLocal<DataReader> INSTANCE = new ThreadLocal<DataReader>() {
        @Override
        protected DataReader initialValue() {
            return new DataReader();
        }
    };

    private static final Class[] COLLECTION_TYPES = new Class[]{
            LinkedHashSet.class,
            TreeSet.class,
            ArrayList.class,
            LinkedList.class,
    };

    private final TypeConverter typeConverter;

    public DataReader() {
        this.typeConverter = new SimpleTypeConverter();
    }

    public <T> T readDataBean(JSONObject json, Class<T> beanType) throws DataReadException {
        try {
            T t = readDataBeanImpl(json, beanType, "");
            return t;
        }
        catch (DataReadException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DataReadException("Unable to marshal JSON into " + beanType.getSimpleName() + ":" + e.getMessage(), e);
        }
    }

    public void readDataBean(JSONObject json, Object bean) throws DataReadException {
        if (bean == null) {
            throw new NullPointerException("bean");
        }
        try {
            readDataBeanImpl(json, bean, "");
        }
        catch (DataReadException e) {
            throw e;
        }
        catch (Exception e) {
            throw new DataReadException("Unable to marshal JSON into " + bean.getClass().getSimpleName() + ":" + e.getMessage(), e);
        }
    }

    private <T> T readDataBeanImpl(JSONObject json, Class<T> beanType, String path) {
        if (beanType.equals(JSONObject.class)) {
            return beanType.cast(json);
        }
        BeanWrapper beanWrapper = new BeanWrapperImpl(beanType);
        readDataBeanImpl(json, beanWrapper, path);
        return beanType.cast(beanWrapper.getWrappedInstance());
    }

    private void readDataBeanImpl(JSONObject json, Object bean, String path) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
        readDataBeanImpl(json, beanWrapper, path);
    }

    private void readDataBeanImpl(JSONObject json, BeanWrapper beanWrapper, String path) {
        for (PropertyDescriptor descriptor : beanWrapper.getPropertyDescriptors()) {
            String name = descriptor.getName();
            Object value = json.opt(name);
            if (value != null) {
                Class<?> type = descriptor.getPropertyType();
                if (Date.class.isAssignableFrom(type) && value instanceof String) {
                    Date date = parseDate((String) value);
                    if (date != null) {
                        beanWrapper.setPropertyValue(name, date);
                        continue;
                    }
                }
                if (BeanUtils.isSimpleValueType(type)) {
                    beanWrapper.setPropertyValue(name, value);
                }
                else if (Collection.class.isAssignableFrom(type) && value instanceof JSONArray) {
                    Object collection = beanWrapper.getPropertyValue(name);
                    Class<?> collectionType = collection instanceof Collection<?> ? collection.getClass() : getDefaultCollection(type);
                    Class<?> elementType = beanWrapper.getPropertyTypeDescriptor(name).getElementTypeDescriptor().getType();
                    collection = readDataCollectionImpl((JSONArray) value, collectionType, elementType, subPath(path, name));
                    beanWrapper.setPropertyValue(name, collection);
                }
                else if (value instanceof JSONObject) {
                    beanWrapper.setPropertyValue(name, readDataBeanImpl((JSONObject) value, type, subPath(path, name)));
                }
            }
        }
    }

    private Date parseDate(String value) {
        if (value == null) {
            return null;
        }
        try {
            return CoreApiDateFormat.DATE_FORMAT.get().parse(value);
        }
        catch (ParseException e) {
            return null;
        }
    }

    private Class<?> getDefaultCollection(Class<?> type) {
        if (!Modifier.isAbstract(type.getModifiers())) {
            return type;
        }
        for (Class<?> c : COLLECTION_TYPES) {
            if (type.isAssignableFrom(c)) {
                return c;
            }
        }
        throw new DataReadException("Do not know how ot create a generic instance of " + type.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    private <E> Collection<E> readDataCollectionImpl(JSONArray jsonArray, Class<?> collectionType, Class<E> elementType, String path) {
        Collection<E> collection = (Collection<E>)BeanUtils.instantiateClass(collectionType);
        boolean simple = BeanUtils.isSimpleValueType(elementType);
        for (int i = 0, l = jsonArray.length(); i < l; i++) {
            Object value = jsonArray.opt(i);
            if (value == null) {
                collection.add(null);
            }
            else if (simple) {
                collection.add(typeConverter.convertIfNecessary(value, elementType));
            }
            else if (value instanceof JSONObject) {
                collection.add(readDataBeanImpl((JSONObject)value, elementType, subPath(path, i)));
            }
            else {
                String msg = String.format("Cannot marshal JSON element of type %s @ %s to a value of type %s",
                        value.getClass().getSimpleName(),
                        subPath(path, i),
                        elementType.getSimpleName());
                throw new DataReadException(msg);
            }
        }
        return collection;
    }

    private String subPath(String path, String child) {
        return path.length() > 0 ? path + "." + child : child;
    }

    private String subPath(String path, int child) {
        return path + "[" + child + "]";
    }
}