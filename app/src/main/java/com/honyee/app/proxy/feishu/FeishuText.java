package com.honyee.app.proxy.feishu;

/**
 * 出文本消息
 * @author wu.dunhong
 */
public class FeishuText implements FeishuMessage{

    @Override
    public String getMsgType() {
        return "text";
    }

    private String text;

    public FeishuText() {
    }

    public FeishuText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
