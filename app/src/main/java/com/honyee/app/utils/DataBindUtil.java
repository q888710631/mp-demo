package com.honyee.app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DataBindUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public DataBindUtil() {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
    }

    public static <T> T bind(Class<T> clz, Map<String, Object> data) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(data), clz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 格式化key
     * hello-world => helloWorld
     * a.hello-world => a.helloWorld
     */
    public static String toCamelCase(String key) {
        // 处理小数点分割的key，效果约等于 split("\\.")
        StringTokenizer tokenizer = new StringTokenizer(key, ".");
        List<String> keyStrings = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String newKey = tokenizer.nextToken();
            if (newKey.contains("-")) {
                newKey = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, newKey);
            } else if (newKey.contains("_")) {
                newKey = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, newKey);
            }
            keyStrings.add(newKey);
        }
        return String.join(".", keyStrings);
    }

    /**
     * 处理Map中的key
     *
     * @param map 例如key为 hello-world
     * @return 转换成 helloWorld
     */
    public static Map<String, Object> dealMapKey(Map<?, ?> map) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (map == null) {
            return result;
        }
        for (Object next : map.keySet()) {
            if (next instanceof String) {
                result.put(toCamelCase(next.toString()), dealValue(map.get(next)));
            } else {
                throw new UnsupportedOperationException("不支持key不为String的情况");
            }
        }
        return result;
    }

    /**
     * 处理value
     */
    private static Object dealValue(Object value) {
        if (value instanceof Map) {
            return dealMapKey((Map<?, ?>) value);
        } else if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            List<Object> collectionResult = new ArrayList<>();
            for (Object child : collection) {
                collectionResult.add(dealValue(child));
            }
            return collectionResult;
        }
        return value;
    }
}
