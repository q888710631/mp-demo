package com.honyee.app.service;

import com.honyee.app.utils.LogUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Profile("kafka-mul")
@Service
public class KafkaMulService implements InitializingBean {

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (kafkaTemplate != null) {
            kafkaTemplate.send("kafka-test", "hello,honyee");
        }
    }

    @KafkaListener(topics = {"kafka-test"}, containerFactory = "listenerContainerFactory")
    public void kafkaTestListener(String result, Acknowledgment acknowledgment) {
        LogUtil.get().info("收到kafka mul消息，{}", result);
        acknowledgment.acknowledge();
    }
}
