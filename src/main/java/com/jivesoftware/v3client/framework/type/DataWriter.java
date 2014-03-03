package com.jivesoftware.v3client.framework.type;

import com.jivesoftware.v3client.framework.entity.AbstractEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ed.venaglia on 3/1/14.
 */
public class DataWriter {

    public static final DataWriter INSTANCE = new DataWriter();

    private static final Map<Class<?>,JsonConverter<?,?>> CONVERTER_MAP = new ConcurrentHashMap<>();

    static {
        // seed the converter map
        addConverter0(Void.class, new NullConverter());
        addConverter0(String.class, new PassThroughConverter<String>());
        addConverter0(Boolean.class, new PassThroughConverter<Boolean>());
        addConverter0(Number.class, new JsonConverter<Number,Number>() {
            @Override
            public Number convert(Number obj) {
                return obj.intValue() == obj.doubleValue()
                        ? obj.intValue()
                        : obj.doubleValue();
            }
        });
        addConverter0(Date.class, new JsonConverter<Date,String>() {
            @Override
            public String convert(Date obj) {
                return CoreApiDateFormat.DATE_FORMAT.get().format(obj);
            }
        });
        addConverter0(AbstractEntity.class, new JsonConverter<AbstractEntity, JSONObject>() {
            @Override
            public JSONObject convert(AbstractEntity obj) {
                JSONObject buffer = new JSONObject();
                INSTANCE.writeDataBean(obj, buffer);
                return buffer;
            }
        });
        addConverter0(Collection.class, new JsonConverter<Collection, JSONArray>() {
            @Override
            public JSONArray convert(Collection obj) {
                JSONArray buffer = new JSONArray();
                INSTANCE.writeDataBeans(obj, buffer);
                return buffer;
            }
        });
        addConverter0(Object[].class, new JsonConverter<Object[], JSONArray>() {
            @Override
            public JSONArray convert(Object[] obj) {
                JSONArray buffer = new JSONArray();
                INSTANCE.writeDataBeans(Arrays.asList(obj), buffer);
                return buffer;
            }
        });
    }

    public void writeDataBean(Object bean, JSONObject buffer) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        for (PropertyDescriptor prop : wrapper.getPropertyDescriptors()) {
            String name = prop.getName();
            Object value = null;
            try {
                value = toJsonValue(prop.getReadMethod().invoke(bean));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (value != null) {
                buffer.put(name, value);
            }
        }
    }

    private void writeDataBeans(Collection<?> beans, JSONArray buffer) {
        for (Object bean : beans) {
            JSONObject obj = new JSONObject();
            buffer.put(obj);
            writeDataBean(bean, obj);
        }
    }

    private <T> Object toJsonValue(T javaValue) {
        if (javaValue == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>)javaValue.getClass();
        JsonConverter<? super T,?> converter = findConverter(type);
        if (converter == null) {
            return null;
        }
        return converter.convert(javaValue);
    }

    private <T> JsonConverter<? super T,?> findConverter(Class<T> type) {
        JsonConverter<?,?> jsonConverter = CONVERTER_MAP.get(type);
        if (jsonConverter == null) {
            jsonConverter = findConverterRecursive(type, new HashSet<Class<?>>());
        }
        //noinspection unchecked
        return (JsonConverter<? super T,?>)jsonConverter;
    }

    private <T> JsonConverter<? super T,?> findConverterRecursive(Class<T> type, Set<Class<?>> visited) {
        if (type == null || visited.contains(type)) {
            return null;
        }
        boolean first = visited.isEmpty();
        visited.add(type);
        JsonConverter<?,?> jsonConverter = CONVERTER_MAP.get(type);
        if (jsonConverter == null) {
            jsonConverter = findConverterRecursive(type.getSuperclass(), visited);
            if (jsonConverter != null) {
                CONVERTER_MAP.put(type, jsonConverter); // cache for classes only, not for interfaces
            } else if (first) {
                for (Class<?> iface : type.getInterfaces()) {
                    jsonConverter = findConverterRecursive(iface, visited);
                    if (jsonConverter != null) break;
                }
            }
        }
        //noinspection unchecked
        return (JsonConverter<? super T,?>)jsonConverter;
    }

    private static <T> void addConverter0(Class<? extends T> type,JsonConverter<T,?> converter) {
        CONVERTER_MAP.put(type, converter);
    }

    private interface JsonConverter<JAVA,JSON> {
        JSON convert(JAVA obj);
    }

    private static class NullConverter implements JsonConverter<Object,Object> {
        @Override
        public Object convert(Object obj) {
            return null;
        }
    }

    private static class PassThroughConverter<TYPE> implements JsonConverter<TYPE,TYPE> {
        @Override
        public TYPE convert(TYPE obj) {
            return obj;
        }
    }
}
