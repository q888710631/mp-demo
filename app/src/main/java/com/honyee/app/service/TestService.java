package com.honyee.app.service;

import com.honyee.app.config.delay.DelayTaskConfiguration;
import com.honyee.app.config.lock.RedisLock;
import com.honyee.app.delay.MyDelayParam;
import com.honyee.app.utils.LogUtil;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class TestService {

    @Resource
    RedissonClient redissonClient;

    @RedisLock(value = "test", key = "#query")
    public String lockTest(String query) {
        LogUtil.info("TestService.test()............");

        MyDelayParam myDelayParam = new MyDelayParam();
        myDelayParam.setId(System.currentTimeMillis());
        myDelayParam.setTitle("延时任务");

        DelayTaskConfiguration.submit(myDelayParam, 5, TimeUnit.SECONDS);
        DelayTaskConfiguration.submit(myDelayParam, Instant.now().plusSeconds(5));

        return "complete";
    }

}
