package com.honyee.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Profile("kafka-single")
@Service
public class KafkaSingleService implements InitializingBean {

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (kafkaTemplate != null) {
            kafkaTemplate.send("kafka-test", "hello,honyee");
        }
    }

    @KafkaListener(topics = {"kafka-test"})
    public void kafkaTestListener(String result, Acknowledgment acknowledgment) {
        log.info("收到kafka single消息，{}", result);
        acknowledgment.acknowledge();
    }
}
