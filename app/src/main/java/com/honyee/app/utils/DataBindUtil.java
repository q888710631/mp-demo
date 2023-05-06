package com.honyee.app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DataBindUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public DataBindUtil() {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
    }

    public static <T> T bind(Class<T> clz, Map<String, Object> data) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
         /*
            待解决：
            复杂嵌套对象转换失败：org.springframework.beans.convertIfNecessary
            conversionnotsupportedexception： 无法将LinkedHashMap转成具体对象
         */
//        T t = clz.getConstructor().newInstance();
//        DataBinder dataBinder = new DataBinder(t);
//        // Set ignored*
//        dataBinder.setIgnoreInvalidFields(true);
//        dataBinder.setIgnoreUnknownFields(true);
//        MutablePropertyValues propertyValues = new MutablePropertyValues(data);
//        dataBinder.bind(propertyValues);
//        return t;
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
            if (newKey.contains("-")) {
                newKey = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, newKey);
            }
            keyStrings.add(newKey);
        }
        return String.join(".", keyStrings);
    }
}
