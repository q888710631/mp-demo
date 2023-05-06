package com.honyee.app.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honyee.app.exp.CommonException;
import com.honyee.app.mapper.QuestionnaireMapper;
import com.honyee.app.model.Questionnaire;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class QuestionnaireService extends ServiceImpl<QuestionnaireMapper, Questionnaire> {

    @Autowired
    private RedissonClient redissonClient;

    public void create(Questionnaire questionnaire, String key) {
        RLock lock = redissonClient.getLock("lock_questionnaire_create_" + key);
        try {
            // 锁定5秒，不解锁
            if (lock.tryLock(5L, TimeUnit.SECONDS)) {

            } else {
                throw new CommonException("请勿频繁提交");
            }
        } catch (InterruptedException e) {
            throw new CommonException(e);
        }
    }
}
