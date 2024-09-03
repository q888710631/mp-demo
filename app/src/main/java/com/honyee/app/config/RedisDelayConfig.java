package com.honyee.app.config;

import cn.hutool.core.date.SystemClock;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonShutdownException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 描述
 *
 * @author wu.dunhong 2024/4/12
 */
@Slf4j
@Component
public class RedisDelayConfig {
    
    private static final String DELAY_QUEUE = "honyee-delay-queue";
    
    @Autowired
    private RedissonClient redissonClient;
    
    @PostConstruct
    public void init() {
        // 创建延时队列
        new Thread(this::consume, "延时消息消费者").start();
    }
    
    public void produce() {
        // redissonClient发送延迟消息
        RBlockingQueue<String> blockingQueue = redissonClient.getBlockingQueue(DELAY_QUEUE);
        RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        delayedQueue.offer(SystemClock.nowDate(), 3, TimeUnit.SECONDS);
    }
    
    /**
     * 将延时消息转发到对应的channel
     */
    public void consume() {
        try {
            while (true) {
                RBlockingQueue<String> blockingQueue = redissonClient.getBlockingQueue(DELAY_QUEUE);
                RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
                String msg = blockingQueue.take();
                log.info("消费：{}", msg);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 保持中断状态
            log.info("延时消息消费线程关闭");
        } catch (RedissonShutdownException e) {
            Thread.currentThread().interrupt();
            log.info("延时消息消费线程关闭");
        }
        
    }
}
