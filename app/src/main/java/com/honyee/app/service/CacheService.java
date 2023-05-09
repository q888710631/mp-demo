package com.honyee.app.service;

import com.honyee.app.dto.TestDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class CacheService {

    @Cacheable(value = "cache-redis#60", key = "#key", cacheManager = "redisCacheManager")
    public TestDTO cacheRedis(String key) {
        System.out.println("cache-redis");
        TestDTO dto = new TestDTO();
        dto.setLocalDateTime(LocalDateTime.now());
        dto.setInstant(Instant.now());
        return dto;
    }

    @Cacheable(value = "cache-memory", key = "#key", cacheManager = "memoryCacheManager")
    public TestDTO cacheMemory(String key) {
        System.out.println("cache-memory");
        TestDTO dto = new TestDTO();
        dto.setLocalDateTime(LocalDateTime.now());
        dto.setInstant(Instant.now());
        return dto;
    }

    @Cacheable(value = "cache-test", key = "#prefix + '_' + #key")
    public String cacheTest(String prefix, String key) {
        return key;
    }

    @CacheEvict(value = "cache-test", key = "#prefix + '_'", allEntries = true)
    public void evictTest(String prefix) {
    }

}
