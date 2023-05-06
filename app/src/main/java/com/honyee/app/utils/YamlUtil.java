package com.honyee.app.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class YamlUtil {
    public static <T> T loadAs(InputStream input, Class<T> clz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(input, Map.class);
        Map<String, Object> result = dealMapKey(map);
        return DataBindUtil.bind(clz, result);
    }

    public static <T> T loadAs(String config, Class<T> clz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(config, Map.class);
        Map<String, Object> result = dealMapKey(map);
        return DataBindUtil.bind(clz, result);
    }

    private static Map<String, Object> dealMapKey(Map map) {
        Set set = map.keySet();
        Iterator iterator = set.iterator();
        Map<String, Object> result = new LinkedHashMap<>();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof String) {
                Object value = map.get(next);
                if (value instanceof Map) {
                    value = dealMapKey((Map) value);
                } else if (value instanceof Collection) {
                    value = dealCollection(value);
                }
                String key = DataBindUtil.toCamelCase(next.toString());
                result.put(key, value);
            } else {
                throw new UnsupportedOperationException("不支持key不为String的情况");
            }
        }
        return result;
    }

    private static Object dealCollection(Object value) {
        if (value instanceof Map) {
            return dealMapKey((Map) value);
        } else if (value instanceof Collection) {
            Collection collection = (Collection) value;
            List collectionResult = new ArrayList();
            for (Object child : collection) {
                collectionResult.add(dealCollection(child));
            }
            return collectionResult;
        }
        return value;
    }
}
