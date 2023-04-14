package com.honyee.app.service;

import org.springframework.cache.annotation.CacheEvict;
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

    @Cacheable(value = "cache-test", key = "#prefix + '_' + #key")
    public String cacheTest(String prefix, String key) {
        return key;
    }

    @CacheEvict(value = "cache-test", key = "#prefix + '_'", allEntries = true)
    public void evictTest(String prefix) {
    }

}
