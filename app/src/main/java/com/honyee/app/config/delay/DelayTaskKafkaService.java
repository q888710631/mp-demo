package com.honyee.app.config.delay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.exp.CommonException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class DelayTaskKafkaService {

    @Resource
    private ObjectMapper objectMapper;

    @KafkaListener(topics = {"delay_task"})
    public void myDelayKafka(String result, Acknowledgment acknowledgment) throws ClassNotFoundException {
        // {"@class":"com.honyee.app.delay.MyDelayParam","id":1680168771748,"title":"myDelayParam"}
        Map<String, Object> map = readResult(result, Map.class);
        Object className = map.get("@class");
        if (className == null) {
            throw new CommonException("延时参数没有配置@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)");
        }
        Class<?> aClass = Class.forName(className.toString());
        Object param = readResult(result, aClass);
        DelayTaskListener delayTaskListener = DelayTaskConfiguration.getDelayTaskListener(className.toString());
        delayTaskListener.run(param);
        // 执行没出错则消费掉
        acknowledgment.acknowledge();
    }

    private <T> T readResult(String result, Class<T> clz) {
        try {
            return objectMapper.readValue(result, clz);
        } catch (JsonProcessingException e) {
            throw new CommonException(e.getMessage());
        }
    }

}
