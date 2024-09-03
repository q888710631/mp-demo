package com.honyee.app.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Profile("kafka-mul")
@Configuration
public class KafkaMulConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "spring.kafka.one")
    public KafkaProperties kafkaPropertiesOne() {
        return new KafkaProperties();
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactoryOne(@Autowired @Qualifier("kafkaPropertiesOne") KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        // kafka地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        // 设置是否自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getConsumer().getEnableAutoCommit());
        // 设置groupId
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        // 一次拉取消息数量
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        // 反序列化
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String> listenerContainerFactory(@Autowired @Qualifier("consumerFactoryOne") ConsumerFactory<Integer, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 设置并发量，小于或等于Topic的分区数
        factory.setConcurrency(1);
        // 设置为批量监听
        factory.setBatchListener(true);
        ContainerProperties properties = factory.getContainerProperties();
        properties.setPollTimeout(3000L);
        properties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateOne(@Autowired @Qualifier("kafkaPropertiesOne") KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(props);

        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        return kafkaTemplate;
    }


}
