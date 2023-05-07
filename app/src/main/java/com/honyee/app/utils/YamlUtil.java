package com.honyee.app.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class YamlUtil {
    public static <T> T loadAs(InputStream input, Class<T> clz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Yaml yaml = new Yaml();
        Map<?, ?> map = yaml.loadAs(input, Map.class);
        Map<String, Object> result = DataBindUtil.dealMapKey(map);
        return DataBindUtil.bind(clz, result);
    }

    public static <T> T loadAs(String config, Class<T> clz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Yaml yaml = new Yaml();
        Map<?, ?> map = yaml.loadAs(config, Map.class);
        Map<String, Object> result = DataBindUtil.dealMapKey(map);
        return DataBindUtil.bind(clz, result);
    }

}
