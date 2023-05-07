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
        StringTokenizer tokenizer = new StringTokenizer(key, ".");
        List<String> keyStrings = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String newKey = tokenizer.nextToken();
            // 不支持横杠和下划线混合处理，通过replace替换一遍
            newKey = newKey.replace("-","_");
            newKey = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, newKey);
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
        Set<?> set = map.keySet();
        Iterator<?> iterator = set.iterator();
        Map<String, Object> result = new LinkedHashMap<>();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof String) {
                Object value = map.get(next);
                if (value instanceof Map) {
                    value = dealMapKey((Map<?, ?>) value);
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

    public static Object dealCollection(Object value) {
        if (value instanceof Map) {
            return dealMapKey((Map<?,?>) value);
        } else if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            List<Object> collectionResult = new ArrayList<>();
            for (Object child : collection) {
                collectionResult.add(dealCollection(child));
            }
            return collectionResult;
        }
        return value;
    }
}
