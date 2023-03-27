package com.honyee.app.config.kafka;


import org.springframework.kafka.listener.ContainerProperties;

public class KafkaProperties {
    // bootstrap-servers
    private String bootstrapServers;

    private Consumer consumer = new Consumer();

    private Listener listener = new Listener();

    public static class Consumer{
        // group-id
        private String groupId;
        // enable-auto-commit
        private String enableAutoCommit;

        public boolean isEnableAutoCommit() {
            return !"false".equals(enableAutoCommit);
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getEnableAutoCommit() {
            return enableAutoCommit;
        }

        public void setEnableAutoCommit(String enableAutoCommit) {
            this.enableAutoCommit = enableAutoCommit;
        }
    }

    public static class Listener{
        // ack-mode
        private ContainerProperties.AckMode ackMode;

        public ContainerProperties.AckMode getAckMode() {
            return ackMode;
        }

        public void setAckMode(ContainerProperties.AckMode ackMode) {
            this.ackMode = ackMode;
        }
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
