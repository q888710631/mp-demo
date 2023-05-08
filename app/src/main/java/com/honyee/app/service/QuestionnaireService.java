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

    @RateLimit(mode = RateLimit.LimitMode.LOCK, lockKey = "#dto.phoneNumber")
    public void create(QuestionnaireCreateDTO dto) {
        System.out.println("QuestionnaireService.create.........");
    }
}
