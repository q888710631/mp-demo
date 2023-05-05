package com.honyee.app.utils;

import com.google.common.base.CaseFormat;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class DataBindUtil {
    public static <T> T bind(Class<T> clz, Map<String, Object> data) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T t = clz.getConstructor().newInstance();
        DataBinder dataBinder = new DataBinder(t);
        // Set ignored*
        dataBinder.setIgnoreInvalidFields(true);
        dataBinder.setIgnoreUnknownFields(true);
        MutablePropertyValues propertyValues = new MutablePropertyValues(data);
        dataBinder.bind(propertyValues);
        return t;
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
