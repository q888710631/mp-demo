package com.honyee.app.service;

import com.honyee.app.config.lock.RedisLock;
import com.honyee.app.utils.LogUtil;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @RedisLock(value = "test", key = "#query")
    public String lockTest(String query) {
        LogUtil.info("TestService.test()............");
        return "complete";
    }
}
