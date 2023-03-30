package com.honyee.app.config.delay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.honyee.app.AppApplication;
import com.honyee.app.delay.MyDelayParam;
import com.honyee.app.exp.CommonException;
import com.honyee.app.utils.LogUtil;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Configuration
public class DelayTaskConfiguration implements DisposableBean {

    public static boolean enable = true;

    // Listener<T>中T的Class.name
    private static final Map<String, DelayTaskListener> QUEUE = new HashMap<>();
    // 已创建的ScheduledExecutorService
    private static final List<ScheduledExecutorService> EXECUTOR_LIST = new ArrayList<>();

    @Value("${application.delay-task.kafka-execute}")
    private Boolean kafkaExecute = Boolean.FALSE;

    @Autowired(required = false)
    private KafkaTemplate kafkaTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void destroy() {
        enable = false;
        for (ScheduledExecutorService executor : EXECUTOR_LIST) {
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        }
    }

    public DelayTaskConfiguration(RedissonClient redissonClient, ObjectProvider<DelayTaskListener<?>> listenerList) {

        for (DelayTaskListener delayTaskListener : listenerList) {
            // 获取接口泛型
            Class<?> tClass = (Class<?>) ((ParameterizedType) delayTaskListener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];

            // 填充队列名称
            QUEUE.put(tClass.getName(), delayTaskListener);

            // 开启线程
            initListener(tClass, delayTaskListener, redissonClient);
        }
    }

    public static DelayTaskListener getDelayTaskListener(String key) {
        return QUEUE.get(key);
    }

    private <T> void initListener(Class<T> tClass, DelayTaskListener<T> delayTaskListener, RedissonClient redissonClient) {
        String name = String.format("%s-%%d", delayTaskListener.getClass().getSimpleName());
        ThreadFactory build = new ThreadFactoryBuilder().setNameFormat(name).build();
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(build);
        EXECUTOR_LIST.add(scheduledExecutorService);
        scheduledExecutorService.submit(() -> {
            String queueName = MyDelayParam.class.getName();
            RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
            //解决服务重启后，无offer，take不到信息。
            redissonClient.getDelayedQueue(blockingFairQueue);
            while (enable) {
                try {
                    T param = blockingFairQueue.take();
                    if (Boolean.TRUE.equals(kafkaExecute) && kafkaTemplate != null) {
                        kafkaTemplate.send("delay_task", objectMapper.writeValueAsString(param));
                    } else {
                        LogUtil.info("延时任务-开始：{}", param);
                        StopWatch stopWatch = new StopWatch();
                        stopWatch.start();
                        delayTaskListener.run(param);
                        stopWatch.stop();
                        long totalTimeMillis = stopWatch.getLastTaskTimeMillis();
                        LogUtil.info("延时任务-结束：时长={}ms，param={}", totalTimeMillis, param);
                    }
                } catch (Exception e) {
                    LogUtil.error("延时任务-异常：{}", e);
                }
            }
        });
    }

    public static void submit(@NotNull Object e, @NotNull Instant at) {
        Instant now = Instant.now();
        if (now.isAfter(at)) {
            throw new CommonException("时间不正确");
        }

        long timeLong = at.getEpochSecond() - now.getEpochSecond();
        submit(e, timeLong, TimeUnit.SECONDS);
    }

    public static void submit(@NotNull Object e, long delay, @NotNull TimeUnit timeUnit) {
        if (e == null) {
            throw new CommonException("不支持无参任务");
        }
        String queueName = e.getClass().getName();
        if (!QUEUE.containsKey(e.getClass().getName())) {
            throw new CommonException(e.getClass().getName() + "缺少对应的DelayTaskListener<T>实现");
        }
        RedissonClient redissonClient = AppApplication.getBean(RedissonClient.class);
        RBlockingQueue<Object> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
        RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.offer(e, delay, timeUnit);
    }
}
