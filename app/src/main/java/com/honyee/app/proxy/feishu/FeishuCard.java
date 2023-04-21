package com.honyee.app.proxy.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.tuple.Pair;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * 卡片消息
 *
 * @author wu.dunhong
 */


public class FeishuCard {
    public String getMsgType() {
        return "interactive";
    }

    @JsonProperty("elements")
    private List<CardElement> elements;

    @JsonProperty("header")
    private Header header;

    public FeishuCard() {
        this.elements = new ArrayList<>();
        this.header = new Header();
    }

    public FeishuCard(String title) {
        this.elements = new ArrayList<>();
        this.header = new Header();
        this.header.title.content = title;
    }

    public FeishuCard appendDivText(String content) {
        Markdown markdown = new Markdown();
        markdown.content = content;
        this.elements.add(markdown);
        return this;
    }

    /**
     * 添加信息
     *
     * @param title   标题
     * @param content 内容
     */
    public List<CardElement> appendElement(String title, String content) {
        Markdown markdown = new Markdown(title, content);
        this.elements.add(markdown);
        return this.elements;
    }

    public interface CardElement {
        String getTag();
    }

    public static class Markdown implements CardElement {
        private String content;

        public Markdown() {
        }

        public Markdown(String title, String text) {
            this.content = String.format("**%s：**%s", title, text);
        }

        @Override
        public String getTag() {
            return "markdown";
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class Div implements CardElement {
        @Override
        public String getTag() {
            return "div";
        }

        private LarkMd text;

        public Div() {
            this.text = new LarkMd();
        }

        /**
         * title加粗： text
         */
        public Div(String title, String text) {
            this.text = new LarkMd(title, text);
        }

        /**
         * 可点击链接的文字
         */
        public Div(String title, URI uri) {
            this.text = new LarkMd(title, uri);
        }

        public LarkMd getText() {
            return text;
        }

        public void setText(LarkMd text) {
            this.text = text;
        }

        public Div text(LarkMd text) {
            this.text = text;
            return this;
        }

    }

    /**
     * lark_md
     * 1. 问题：英文引号会被转成中文引号
     * 2. 功能：支持@
     */
    public static class LarkMd implements CardElement {
        @Override
        public String getTag() {
            return "lark_md";
        }

        private String content;

        public LarkMd() {
        }

        /**
         * title加粗： text
         */
        public LarkMd(String title, String text) {
            this.content = String.format("**%s：**%s", title, text);
        }

        /**
         * 可点击链接的文字
         */
        public LarkMd(String text, URI uri) {
            this.content = String.format("(%s)\n%s", text, uri);
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public LarkMd content(String content) {
            this.content = content;
            return this;
        }

    }

    /**
     * 分割线
     */
    public static class Hr implements CardElement {
        @Override
        public String getTag() {
            return "hr";
        }
    }

    /**
     * 按钮
     */
    public static class Button implements CardElement {
        @Override
        public String getTag() {
            return "button";
        }

        private Text text;

        /**
         * default、primary、danger
         */
        private String type;

        /**
         * 跳转链接
         */
        private String url;

        /**
         * 点击时生成的参数 key:value，用于actions组件
         */
        private Pair<String,String> value;

        public Button() {
            this.type = "default";
        }

        public Text getText() {
            return text;
        }

        public void setText(Text text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Pair<String, String> getValue() {
            return value;
        }

        public void setValue(Pair<String, String> value) {
            this.value = value;
        }
    }

    public static class Text implements CardElement {

        @JsonProperty("content")
        private String content;

        public String getTag() {
            return "plain_text";
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class Header {
        private String template;
        private Text title;

        public Header() {
            info();
            this.title = new Text();
        }

        /**
         * 警告、黄色
         */
        public void warn() {
            this.template = "yellow";
        }

        /**
         * 错误、红色
         */
        public void error() {
            this.template = "red";
        }

        /**
         * 成功、绿色
         */
        public void success() {
            this.template = "green";
        }

        /**
         * 信息、蓝色
         */
        public void info() {
            this.template = "blue";
        }
        /**
         * 特殊、紫色
         */
        public void special() {
            this.template = "purple";
        }

        public String getTemplate() {
            return template;
        }

        public void setTemplate(String template) {
            this.template = template;
        }

        public Text getTitle() {
            return title;
        }

        public void setTitle(Text title) {
            this.title = title;
        }
    }

    public List<CardElement> getElements() {
        return elements;
    }

    public void setElements(List<CardElement> elements) {
        this.elements = elements;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
}
