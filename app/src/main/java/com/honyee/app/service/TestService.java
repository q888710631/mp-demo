package com.honyee.app.service;

import com.honyee.app.config.delay.DelayTaskConfiguration;
import com.honyee.app.config.lock.RedisLock;
import com.honyee.app.delay.MyDelayParam;
import com.honyee.app.utils.LogUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class TestService {

    @RedisLock(value = "test", key = "#query")
    public String lockTest(String query) {
        LogUtil.info("TestService.test()............");
        return "complete";
    }

}
