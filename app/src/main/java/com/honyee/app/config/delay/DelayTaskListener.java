package com.honyee.app.config.delay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
public abstract class DelayTaskListener<T> {

    StopWatch stopWatch = new StopWatch();

    public abstract void execute(T t);

    public void run(T t) {
        log.info("延时任务-开始：{}", t);
        stopWatch.start();
        execute(t);
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getLastTaskTimeMillis();
        log.info("延时任务-结束：时长={}ms，param={}", totalTimeMillis, t);
    }

}