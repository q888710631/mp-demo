package com.honyee.app.proxy.feishu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeishuMessageRequeset {

    /**
     * 标题
     */
    protected String title;

    /**
     * 通知的机器人token
     */
    protected String token;

    /**
     * 机器人加签密钥
     */
    protected String secret;

    /**
     * 引用内容，markdown模式有效
     */
    protected String reference;

    /**
     * 消息内容
     */
    protected List<KeyValue> msg = new ArrayList<>();

    /**
     * @设置
     */
    protected Map<String, Object> at = new HashMap<>();

    /**
     * 分组
     */
    protected String group = "";

    /**
     * 消息等级
     */
    protected MessageLevelEnum level;

    @SuppressWarnings("unchecked")
    public FeishuMessageRequeset addAt(String phone) {
        if (phone == null) {
            throw new NullPointerException("phone is marked non-null but is null");
        }
        List<String> atMobiles = (List<String>) this.at.computeIfAbsent("atMobiles", k -> new ArrayList<>());
        atMobiles.add(phone);
        return this;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public FeishuMessageRequeset setAtAll() {
        this.at.put("isAtAll", true);
        return this;
    }

    public FeishuMessageRequeset() {
        this.level = MessageLevelEnum.INFO;
    }

    public FeishuMessageRequeset addMsg(String key, String value) {
        this.msg.add(new KeyValue(key, value));
        return this;
    }

    public FeishuMessageRequeset emptyLine() {
        this.msg.add(new KeyValue());
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public String getToken() {
        return this.token;
    }

    public String getSecret() {
        return this.secret;
    }

    public List<KeyValue> getMsg() {
        return this.msg;
    }

    public Map<String, Object> getAt() {
        return this.at;
    }

    public FeishuMessageRequeset setTitle(String title) {
        this.title = title;
        return this;
    }

    public FeishuMessageRequeset setToken(String token) {
        this.token = token;
        return this;
    }

    public FeishuMessageRequeset setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    public FeishuMessageRequeset setMsg(List<KeyValue> msg) {
        this.msg = msg;
        return this;
    }

    public FeishuMessageRequeset setAt(Map<String, Object> at) {
        this.at = at;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public MessageLevelEnum getLevel() {
        return level;
    }

    public void setLevel(MessageLevelEnum level) {
        this.level = level;
    }

    public FeishuMessageRequeset level(MessageLevelEnum level) {
        this.level = level;
        return this;
    }

    public FeishuMessageRequeset info() {
        this.level = MessageLevelEnum.INFO;
        return this;
    }

    public FeishuMessageRequeset success() {
        this.level = MessageLevelEnum.SUCCESS;
        return this;
    }

    public FeishuMessageRequeset warn() {
        this.level = MessageLevelEnum.WARN;
        return this;
    }

    public FeishuMessageRequeset error() {
        this.level = MessageLevelEnum.ERROR;
        return this;
    }

    public FeishuMessageRequeset special() {
        this.level = MessageLevelEnum.SPECIAL;
        return this;
    }
    
    public static class KeyValue {
        private String key;
        private String value;

        public KeyValue() {
        }

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public KeyValue setKey(String key) {
            this.key = key;
            return this;
        }

        public KeyValue setValue(String value) {
            this.value = value;
            return this;
        }

    }
}
