package com.honyee.app.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.honyee.app.config.limit.RateLimit;
import com.honyee.app.dto.QuestionnaireCreateDTO;
import com.honyee.app.exp.CommonException;
import com.honyee.app.mapper.QuestionnaireMapper;
import com.honyee.app.model.Questionnaire;
import com.honyee.app.utils.LogUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class QuestionnaireService extends ServiceImpl<QuestionnaireMapper, Questionnaire> {

    @Autowired
    private RedissonClient redissonClient;

    public void create(QuestionnaireCreateDTO dto) {
        String key = dto.getPhoneNumber();
        RLock lock = redissonClient.getLock("lock_questionnaire_create_" + key);
        try {
            // 锁定5秒，不解锁
            if (lock.isLocked()) {
                throw new CommonException("请勿频繁提交");
            }
            if (lock.tryLock(1L,5L, TimeUnit.SECONDS)) {
                LogUtil.info("我拿到执行权了");
            }
        } catch (InterruptedException e) {
            throw new CommonException(e);
        }
    }
}
