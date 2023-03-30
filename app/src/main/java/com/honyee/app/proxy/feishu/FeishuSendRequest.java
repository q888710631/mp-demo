package com.honyee.app.proxy.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author wu.dunhong
 */
public class FeishuSendRequest {
    @JsonProperty("msg_type")
    private String msgType;

    @JsonProperty("content")
    private FeishuMessage content;

    @JsonProperty("card")
    private FeishuCard card;

    public FeishuSendRequest() {
    }

    public static FeishuSendRequest build(FeishuMessage feishuMessage) {
        FeishuSendRequest dto = new FeishuSendRequest();
        dto.msgType = feishuMessage.getMsgType();
        dto.content = feishuMessage;
        return dto;
    }

    public static FeishuSendRequest build(FeishuCard feishuCard) {
        FeishuSendRequest dto = new FeishuSendRequest();
        dto.msgType = feishuCard.getMsgType();
        dto.card = feishuCard;
        return dto;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public FeishuMessage getContent() {
        return content;
    }

    public void setContent(FeishuMessage content) {
        this.content = content;
    }

    public FeishuCard getCard() {
        return card;
    }

    public void setCard(FeishuCard card) {
        this.card = card;
    }
}
