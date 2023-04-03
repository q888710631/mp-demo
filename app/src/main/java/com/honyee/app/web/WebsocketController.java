package com.honyee.app.web;

import com.honyee.app.exp.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Profile("websocket")
@RestController
public class WebsocketController {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/send-msg") // 相当于@RequestMapping
    @SendToUser("/topic/notifications") // 返回值发送给请求消息的人
    @SendTo("/topic/notifications") // 返回值发送给所有人
    public String sendMsg(MessageEntity messageEntity) {
        // 点对点发送给user-name=honyee的subscribe
        // template.convertAndSendToUser("honyee","/topic/notifications", "hello honyee");
        // 点对点发送给subscribe
        // template.convertAndSend("/topic/notifications", "hello everybody");

        if (true) throw new CommonException("故意的异常");
        return "我是返回值";
    }

    /**
     * 异常处理，@MessageExceptionHandler可指定处理的异常
     * 只有在同一个Controller里才能处理？？
     */
    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public String exceptionHandle(Throwable throwable) {
        return throwable.getMessage();
    }

    public static class MessageEntity {

        private String from;
        private String to;
        private String message;
        private Date time;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }
    }

}