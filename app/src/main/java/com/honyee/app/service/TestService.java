package com.honyee.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.config.delay.DelayTask;
import com.honyee.app.config.lock.RedisLock;
import com.honyee.app.utils.LogUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class TestService {

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    ObjectMapper objectMapper;

    @RedisLock(value = "test", key = "#query")
    public String lockTest(String query)  {
        LogUtil.info("TestService.test()............");
        DelayTask delayTask = new DelayTask();
        delayTask.setId(System.currentTimeMillis());
        delayTask.setTitle("延迟任务");
        delayTask.setTask(() -> {
            System.out.println(LocalDateTime.now());
        });
        // https://blog.csdn.net/weixin_51110874/article/details/123288473
        // https://blog.csdn.net/WantFlyDaCheng/article/details/120558888
        try {
            redisTemplate.opsForZSet().add("delay_task", objectMapper.writeValueAsString(objectMapper), System.currentTimeMillis() + 30_0000L);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return "complete";
    }
}
