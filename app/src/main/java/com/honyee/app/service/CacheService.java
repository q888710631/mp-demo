package com.honyee.app.service;

import com.honyee.app.dto.TestDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Cacheable(value = "cache-redis#60", key = "#key", cacheManager = "redisCacheManager")
    public TestDTO cacheRedis(String key) {
        System.out.println("cache-redis");
        return new TestDTO();
    }

    @Cacheable(value = "cache-memory", key = "#key", cacheManager = "memoryCacheManager")
    public TestDTO cacheMemory(String key) {
        System.out.println("cache-memory");
        return new TestDTO();
    }

    @Cacheable(value = "cache-test", key = "#prefix + '_' + #key")
    public String cacheTest(String prefix, String key) {
        return key;
    }

    @CacheEvict(value = "cache-test", key = "#prefix + '_'", allEntries = true)
    public void evictTest(String prefix) {
    }

}
