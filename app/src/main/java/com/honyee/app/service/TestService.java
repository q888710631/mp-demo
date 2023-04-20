package com.honyee.app.service;

import com.honyee.app.config.delay.DelayTaskConfiguration;
import com.honyee.app.config.lock.RedisLock;
import com.honyee.app.delay.MyDelayParam;
import com.honyee.app.utils.LogUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class TestService {
    @Resource
    private CacheService cacheService;

    @RedisLock(value = "test", key = "#query")
    public String lockTest(String query) {
        LogUtil.info("TestService.test()............");
        MyDelayParam myDelayParam = new MyDelayParam();
        myDelayParam.setId(System.currentTimeMillis());
        myDelayParam.setTitle("myDelayParam");
        for (int i = 0; i < 10; i++) {
            DelayTaskConfiguration.submit(myDelayParam, i + 2, TimeUnit.SECONDS);
        }
        return "complete";
    }

    int i = 0;
    String[] arr = {"first", "twice", "third"};

    public void cacheTest() {
        long currentTimeMillis = System.currentTimeMillis();
        // 批量添加缓存
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < 3; j++) {
                cacheService.cacheTest(arr[i], currentTimeMillis + j + "");
            }
        }
    }

    public void evictTest() {
        i = (i + 1) % arr.length;
        cacheService.evictTest(arr[i]);
    }
}
