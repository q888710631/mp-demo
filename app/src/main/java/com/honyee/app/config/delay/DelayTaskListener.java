package com.honyee.app.config.delay;

import com.honyee.app.utils.LogUtil;
import org.springframework.util.StopWatch;

public abstract class DelayTaskListener<T> {
    StopWatch stopWatch = new StopWatch();

    public abstract void execute(T t);

    public void run(T t) {
        LogUtil.info("延时任务-开始：{}", t);
        stopWatch.start();
        execute(t);
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getLastTaskTimeMillis();
        LogUtil.info("延时任务-结束：时长={}ms，param={}", totalTimeMillis, t);
    }

}