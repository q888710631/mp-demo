package com.honyee.app.delay;

import com.honyee.app.config.delay.DelayTaskListener;
import com.honyee.app.utils.LogUtil;
import org.springframework.stereotype.Component;

@Component
public class MyDelayTaskListener implements DelayTaskListener<MyDelayParam> {

    @Override
    public void run(MyDelayParam myDelayParam) {
        LogUtil.info("{}开始执行任务->{}", this.getClass().getSimpleName(), myDelayParam.getTitle());
        try {
            Thread.sleep(1234);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
