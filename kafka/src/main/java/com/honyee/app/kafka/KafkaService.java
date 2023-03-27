package com.honyee.app.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @KafkaListener(topics = {"kafka-test"})
    public void test(String result, Acknowledgment acknowledgment) {
        System.out.println();
    }
}
