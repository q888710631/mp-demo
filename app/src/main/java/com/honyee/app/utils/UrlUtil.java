package com.honyee.app.utils;

import com.google.common.base.CaseFormat;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.DataBinder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * 依赖common-lang3、guava、spring
 */
public class UrlUtil {
    
    private UrlUtil() {
    }
    
    /**
     * 读取url的query参数，并转成dto
     */
    public static <T> T readQuery(String url, Class<T> clz) throws ReflectiveOperationException {
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromHttpUrl(url).build().getQueryParams();
        Map<String, Object> parameters = getParameters(queryParams);
        T t = clz.getConstructor().newInstance();
        DataBinder dataBinder = new DataBinder(t);
        // Set ignored*
        dataBinder.setIgnoreInvalidFields(true);
        dataBinder.setIgnoreUnknownFields(true);
        MutablePropertyValues propertyValues = new MutablePropertyValues(parameters);
        dataBinder.bind(propertyValues);
        return t;
    }
    
    /**
     * 将MultiValueMap转成MutablePropertyValues可用的Map
     */
    private static Map<String, Object> getParameters(MultiValueMap<String, String> map) {
        Map<String, Object> params = new TreeMap<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String paramName = toCamelCase(entry.getKey());
            List<String> values = entry.getValue();
            if (values == null || values.isEmpty()) {
                // Do nothing, no values found at all.
            } else if (values.size() > 1) {
                params.put(paramName, values);
            } else {
                params.put(paramName, values.get(0));
            }
        }
        return params;
    }
    
    /**
     * 格式化key
     * hello-world => helloWorld     * a.hello-world => a.helloWorld
     */
    private static String toCamelCase(String key) {
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