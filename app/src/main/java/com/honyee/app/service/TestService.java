package com.honyee.app.service;

import com.honyee.app.utils.LogUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Profile("dev")
@Service
public class TestService implements InitializingBean {

    @Autowired(required = false)
    KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (kafkaTemplate != null) {
            kafkaTemplate.send("kafka-test", "hello,honyee");
        }
    }

    @KafkaListener(topics = {"kafka-test"})
    public void kafkaTestListener(String result, Acknowledgment acknowledgment) {
        LogUtil.get().info("收到kafka消息，{}", result);
        acknowledgment.acknowledge();
    }
}
