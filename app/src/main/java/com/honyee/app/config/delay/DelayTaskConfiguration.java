package com.honyee.app.config.delay;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Configuration
public class DelayTaskConfiguration implements DisposableBean {

    private final RedissonClient redissonClient;

    public static boolean enable = true;

    // Listener<T>中T的Class.name
    private static final Set<String> QUEUE_NAMES = new HashSet<>();

    @Override
    public void destroy() {
        enable = false;
    }

    public DelayTaskConfiguration(RedissonClient redissonClient, ObjectProvider<DelayTaskListener<?>> listenerList) {
        this.redissonClient = redissonClient;

        for (DelayTaskListener delayTaskListener : listenerList) {
            // 获取接口泛型
            Class<?> tClass = (Class<?>) ((ParameterizedType) delayTaskListener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];

            // 填充队列名称
            QUEUE_NAMES.add(tClass.getName());

            // 开启线程
            initListener(tClass, delayTaskListener, redissonClient);
        }
    }

    private <T> void initListener(Class<T> tClass, DelayTaskListener<T> delayTaskListener, RedissonClient redissonClient) {
        String name = String.format("%s-%%d", delayTaskListener.getClass().getSimpleName());
        ThreadFactory build = new ThreadFactoryBuilder().setNameFormat(name).build();
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(build);

        scheduledExecutorService.submit(() -> {
            String queueName = MyDelayParam.class.getName();
            RBlockingQueue<T> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
            //解决服务重启后，无offer，take不到信息。
            redissonClient.getDelayedQueue(blockingFairQueue);
            while (enable) {
                try {
                    T param = blockingFairQueue.take();
                    LogUtil.info("延时任务-开始：{}", param);
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    delayTaskListener.run(param);
                    stopWatch.stop();
                    long totalTimeMillis = stopWatch.getTotalTimeMillis();
                    LogUtil.info("延时任务-结束：时长={}ms，param={}", totalTimeMillis, param);
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

        long l = at.getEpochSecond() - now.getEpochSecond();
        submit(e, l, TimeUnit.SECONDS);
    }

    public static void submit(@NotNull Object e, long delay, @NotNull TimeUnit timeUnit) {
        if (e == null) {
            throw new CommonException("不支持无参任务");
        }
        String queueName = e.getClass().getName();
        if (!QUEUE_NAMES.contains(e.getClass().getName())) {
            throw new CommonException(e.getClass().getName() + "缺少对应的DelayTaskListener<T>实现");
        }
        RedissonClient redissonClient = AppApplication.getBean(RedissonClient.class);
        RBlockingQueue<Object> blockingFairQueue = redissonClient.getBlockingQueue(queueName);
        RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.offer(e, delay, timeUnit);
    }
}
