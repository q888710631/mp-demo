package com.honyee.app.config.http;

import com.honyee.app.service.WebsocketService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocketConfiguration 与 WebSocketConfiguration2 选其中一种即可
 */
@Profile("websocket")
@EnableWebSocket
@Configuration
public class WebSocketConfiguration implements WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/websocket").setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new WebsocketService();
    }

}
