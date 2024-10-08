package com.honyee.app.config.delay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.honyee.app.delay.MyDelayParam;
import com.honyee.app.exp.CommonException;
import com.honyee.app.utils.LogUtil;
import com.honyee.app.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonShutdownException;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Configuration
public class DelayTaskConfiguration implements DisposableBean {

    public static boolean enable = true;

    // Listener<T>中T的Class.name
    private static final Map<String, DelayTaskListener> QUEUE = new HashMap<>();
    // 已创建的ScheduledExecutorService
    private static final List<ScheduledExecutorService> EXECUTOR_LIST = new ArrayList<>();
    // 是否使用kafka运行
    private boolean enableKafkaRun = false;
    // 线程池
    private final Executor executor;
    // 子类泛型
    private static final Map<String, Class<?>> SUB_CLASS_T = new HashMap<>();

    private final KafkaTemplate kafkaTemplate;

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

    public DelayTaskConfiguration(@Value("${application.delay-task.kafka-execute}") Boolean kafkaExecute,
                                  BeanFactory beanFactory,
                                  RedissonClient redissonClient,
                                  @Autowired(required = false) KafkaTemplate kafkaTemplate,
                                  ObjectProvider<DelayTaskListener<?>> listenerList) {
        this.enableKafkaRun = Boolean.TRUE.equals(kafkaExecute) && kafkaTemplate != null;
        this.kafkaTemplate = kafkaTemplate;

        if (this.enableKafkaRun) {
            executor = null;
        } else {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(5); //核心线程数
            executor.setMaxPoolSize(10);  //最大线程数
            executor.setQueueCapacity(1000); //队列大小
            executor.setKeepAliveSeconds(300); //线程最大空闲时间
            executor.setThreadNamePrefix("my-delay-task-Executor-"); // 指定用于新创建的线程名称的前缀
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略（一共四种，此处省略）
            executor.initialize();
            // sleuth包装自定义线程池：自定义的异步任务线程池，会导致Sleuth无法新创建一个Span，需要通过LazyTraceExecutor包裹一层
            this.executor = new LazyTraceExecutor(beanFactory, executor);
        }

        for (DelayTaskListener delayTaskListener : listenerList) {
            // 获取泛型
            Class<? extends DelayTaskListener> aClass = delayTaskListener.getClass();
            ParameterizedType genericSuperclass = (ParameterizedType) aClass.getGenericSuperclass();
            Class<?> tClass = (Class<?>) genericSuperclass.getActualTypeArguments()[0];
            // 填充子类泛型
            SUB_CLASS_T.put(tClass.getName(), tClass);
            // 填充队列名称
            QUEUE.put(tClass.getName(), delayTaskListener);

            // 开启线程
            initListener(tClass, delayTaskListener, redissonClient);
        }
    }

    /**
     * 获取延迟任务监听
     */
    public static DelayTaskListener getDelayTaskListener(String className) {
        return QUEUE.get(className);
    }

    /**
     * 获取延迟任务参数Class
     */
    public static Class<?> getDelayTaskParamClass(String className) {
        return SUB_CLASS_T.get(className);
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
                    if (this.enableKafkaRun) {
                        kafkaTemplate.send("delay_task", objectMapper.writeValueAsString(param));
                    } else {
                        executor.execute(() -> delayTaskListener.run(param));
                    }
                } catch (RedissonShutdownException e) {
                    log.error("延时任务-异常：{}", e.getMessage());
                } catch (Exception e) {
                    log.error("延时任务-异常：{}", e.getMessage(), e);
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
        RedissonClient redissonClient = SpringUtil.getBean(RedissonClient.class);
        RBlockingQueue<Object> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
        RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.offer(e, delay, timeUnit);
    }
}
