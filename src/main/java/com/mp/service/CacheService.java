package com.mp.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Cacheable(value = "cache-redis#60", key = "#key")
    public void cacheRedis(String key) {
        System.out.println("cache-redis");
    }


    @Cacheable(value = "cache-memory", key = "#key", cacheManager = "memoryCacheManager")
    public void cacheMemory(String key) {
        System.out.println("cache-memory");
    }


}
