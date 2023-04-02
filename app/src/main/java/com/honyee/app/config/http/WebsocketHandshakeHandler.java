package com.honyee.app.config.http;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@Profile("websocket")
@Component
public class WebsocketHandshakeHandler extends DefaultHandshakeHandler {

    /**
     * 鉴权，获取不到自定义的header
     */
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletServerHttpRequest.getServletRequest();
            final String token = httpRequest.getParameter("token");
            if (StringUtils.isNotBlank(token)) {
                return () -> "honyee";
            }
        }
        // todo 鉴权失败后返回null，在WebsocketAuthChannelInterceptor会手动抛出异常
        return () -> "anonymous";
//        return null;

    }

}