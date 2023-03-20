package com.mp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mp.config.seurity.AuthenticateProperties;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class CacheService implements ApplicationRunner {

    @Cacheable(value = "cache-redis#60", key = "#key")
    public void cacheRedis(String key) {
        System.out.println("cache-redis");
    }


    @Cacheable(value = "cache-memory", key = "#key", cacheManager = "memoryCacheManager")
    public void cacheMemory(String key) {
        System.out.println("cache-memory");
    }

    @Resource
    ResourceLoader resourceLoader;

    @Resource
    ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Yaml yaml = new Yaml();
        ClassPathResource application = new ClassPathResource("authentication.yml");
        Map<String, Object> data = yaml.load(new FileInputStream(application.getFile()));
        String json = objectMapper.writeValueAsString(data);
        AuthenticateProperties authenticateProperties = objectMapper.readValue(json, AuthenticateProperties.class);
        System.out.println();
    }
}
