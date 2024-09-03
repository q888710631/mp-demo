package com.honyee.app.delay;

import com.honyee.app.config.delay.DelayTaskListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyDelayTaskListener extends DelayTaskListener<MyDelayParam> {

    @Override
    public void execute(MyDelayParam myDelayParam) {
        log.info("{}开始执行任务->{}", this.getClass().getSimpleName(), myDelayParam.getTitle());
        try {
            Thread.sleep(1234);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
