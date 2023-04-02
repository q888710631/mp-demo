package com.honyee.app.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class WebsocketController {


    // 这里的 @MessageMapping 可以当成 @RequestMapping,
    // 当有信息 (sendMsg 方法中的 messageEntity 参数即为客服端发送的信息实体)
    // 发送到 /sendMsg 时会在这里进行处理
    @MessageMapping("/sendMsg")
    public void sendMsg(MessageEntity messageEntity) {
        System.out.println();
    }

    public static class MessageEntity {

        private Long from;
        private Long to;
        private String message;
        private Date time;

        public Long getFrom() {
            return from;
        }

        public void setFrom(Long from) {
            this.from = from;
        }

        public Long getTo() {
            return to;
        }

        public void setTo(Long to) {
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