package com.honyee.app.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class YamlUtil {
    public static <T> T loadAs(String config, Class<T> clz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(config, Map.class);
        Map<String, Object> result = dealKey(map);
        return DataBindUtil.bind(clz, result);
    }

    private static Map<String, Object> dealKey(Map map) {
        Set set = map.keySet();
        Iterator iterator = set.iterator();
        Map<String, Object> result = new HashMap<>();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof String) {
                Object value = map.get(next);
                if (value instanceof Map) {
                    value = dealKey((Map) value);
                }
                String key = DataBindUtil.toCamelCase(next.toString());
                result.put(key, value);
            } else {
                throw new UnsupportedOperationException("不支持key不为String的情况");
            }
        }
        return result;
    }
}
