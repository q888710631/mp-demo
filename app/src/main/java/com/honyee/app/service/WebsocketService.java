package com.honyee.app.service;

import com.honyee.app.utils.LogUtil;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebsocketService extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LogUtil.info("Websocket建立连接：{}", session.getRemoteAddress());
        SESSION_MAP.put(session.getRemoteAddress().toString(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        LogUtil.info("Websocket消息：{}", message);
    }
}
