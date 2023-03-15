package com.mp.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Cacheable(value = "cache#60", key = "#key")
    public void cache(String key) {
        System.out.println("cache");
    }
}
