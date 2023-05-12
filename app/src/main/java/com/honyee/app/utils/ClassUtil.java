package com.honyee.app.utils;

import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ClassUtil {

    public static Set<Class<?>> getTypesAnnotatedWith(String packageName, Class<? extends Annotation> annotation) {
        // 需要ConfigurationBuilder包装，否则打成jar包后无法扫描到Class
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage(packageName));
        return reflections.getTypesAnnotatedWith(annotation);
    }
}
