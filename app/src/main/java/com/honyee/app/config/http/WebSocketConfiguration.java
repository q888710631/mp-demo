package com.honyee.app.config.http;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.Resource;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Resource
    private WebsocketHandshakeHandler websocketHandshakeHandler;

    @Resource
    private WebsocketAuthChannelInterceptor websocketAuthChannelInterceptor;

    @Resource
    private WebsocketHandshakeInterceptor websocketHandShakeInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 开启一个简单的基于内存的消息代理
        // 将消息返回到订阅了带 /chat 前缀的目的客户端
        config.enableSimpleBroker("/chat");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOrigins("*")
                .setHandshakeHandler(websocketHandshakeHandler)
                .addInterceptors(websocketHandShakeInterceptor)
        ;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(websocketAuthChannelInterceptor);
    }

}