package com.honyee.app.service;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class MyKeyGenerator implements KeyGenerator {
    static int i = 0;
    static String[] arr = {"way_", "home_"};

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return arr[i++ % arr.length] + System.currentTimeMillis();
    }

}
