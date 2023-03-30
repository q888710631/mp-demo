package com.honyee.app.proxy.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * 富文本消息
 * @author wu.dunhong
 */
public class FeishuRtf implements FeishuMessage {

    @Override
    public String getMsgType() {
        return "post";
    }

    @JsonProperty("post")
    private Post post;

    public FeishuRtf() {
        this.post = new Post();
    }
    public FeishuRtf(String title) {
        this.post = new Post(title);
    }

    public ZhCn getZhCnForAppend() {
        return this.post.zhCn;
    }

    /**
     * 新起一行
     */
    public List<Content> newLine() {
        List<Content> list = new ArrayList<>();
        this.post.zhCn.content.add(list);
        return list;
    }

    /**
     * 新起一行，并追加
     */
    public List<Content> newLine(Content content) {
        List<Content> list = newLine();
        list.add(content);
        return list;
    }

    /**
     * 新起一行，并追加
     */
    public ZhCn newLine(String title, String text) {
        List<Content> list = newLine();
        list.add(new Text(title));
        list.add(new Text(text));
        return this.post.zhCn;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public static class Post {
        @JsonProperty("zh_cn")
        private ZhCn zhCn;

        public Post() {
            this.zhCn = new ZhCn();
        }

        public Post(String title) {
            this.zhCn = new ZhCn(title);
        }

        public ZhCn getZhCn() {
            return zhCn;
        }

        public void setZhCn(ZhCn zhCn) {
            this.zhCn = zhCn;
        }
    }


    public static class ZhCn {
        @JsonProperty("title")
        private String title;

        @JsonProperty("content")
        private List<List<Content>> content;

        public ZhCn() {
            this.content = new ArrayList<>();
        }

        public ZhCn(String title) {
            this();
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<List<Content>> getContent() {
            return content;
        }

        public void setContent(List<List<Content>> content) {
            this.content = content;
        }
    }

    /**
     * 基础接口
     */
    interface Content {
        String getTag();
    }

    /**
     * 文本
     */
    public static class Text implements Content {
        private String text;

        public Text() {
        }

        public Text(String text) {
            this.text = text;
        }

        @Override
        public String getTag() {
            return "text";
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }


    /**
     * 超链接
     */
    public static class A implements Content {
        private String text;
        private String href;

        public A() {
        }

        public A(String text, String href) {
            this.text = text;
            this.href = href;
        }

        @Override
        public String getTag() {
            return "a";
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }

    /**
     * 图片
     */
    public static class Image implements Content {
        @JsonProperty("image_key")
        private String imageKey;

        public Image() {
        }

        public Image(String imageKey) {
            this.imageKey = imageKey;
        }

        @Override
        public String getTag() {
            return "img";
        }

        public String getImageKey() {
            return imageKey;
        }

        public void setImageKey(String imageKey) {
            this.imageKey = imageKey;
        }
    }


    /**
     * 多媒体
     */
    public static class Media implements Content {
        @JsonProperty("image_key")
        private String imageKey;
        @JsonProperty("file_key")
        private String fileKey;

        public Media() {
        }

        public Media(String imageKey, String fileKey) {
            this.imageKey = imageKey;
            this.fileKey = fileKey;
        }

        @Override
        public String getTag() {
            return "media";
        }

        public String getImageKey() {
            return imageKey;
        }

        public void setImageKey(String imageKey) {
            this.imageKey = imageKey;
        }

        public String getFileKey() {
            return fileKey;
        }

        public void setFileKey(String fileKey) {
            this.fileKey = fileKey;
        }
    }

    /**
     * emotion 表情
     */
    public static class Emotion implements Content {
        @JsonProperty("emoji_type")
        private String emojiType;

        public Emotion() {
        }

        public Emotion(String emojiType) {
            this.emojiType = emojiType;
        }

        @Override
        public String getTag() {
            return "emotion";
        }

        public String getEmojiType() {
            return emojiType;
        }

        public void setEmojiType(String emojiType) {
            this.emojiType = emojiType;
        }
    }


}
