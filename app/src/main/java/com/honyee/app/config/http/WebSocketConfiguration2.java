package com.honyee.app.config.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocketConfiguration 与 WebSocketConfiguration2 选其中一种即可
 */
@Profile("websocket")
@Configuration
public class WebSocketConfiguration2 {

    /**
     * 给spring容器注入这个ServerEndpointExporter对象
     * <p>
     * 检测所有带有@serverEndpoint注解的bean并注册他们。
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
