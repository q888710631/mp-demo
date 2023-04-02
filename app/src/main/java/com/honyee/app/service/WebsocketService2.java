package com.honyee.app.service;

import com.honyee.app.utils.LogUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 必须是@Component而不能时@Service
 */
@Component
@ServerEndpoint("/websocket2")
public class WebsocketService2 {
    private static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        LogUtil.info("Websocket2建立连接");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        LogUtil.info("Websocket2消息：{}", message);
    }

}
