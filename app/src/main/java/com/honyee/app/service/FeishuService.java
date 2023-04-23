package com.honyee.app.service;

import com.honyee.app.proxy.feishu.*;
import com.honyee.app.utils.LogUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URI;

/**
 * 飞书机器人
 * <p>
 * 参考文档 https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/im-v1/message/create_json#45e0953e
 */
@Service
public class FeishuService {

    public static final String URL = "https://open.feishu.cn/open-apis/bot/v2/hook/";

    @Value("${application.feishu.group.common}")
    private String hookCommon;

    @Value("${application.feishu.group.log-notify}")
    private String hookLogNotify;

    @Resource
    private FeishuProxy feishuProxy;

    /**
     * 请求的URI
     *
     * @param group 用于兼容原钉钉的分组
     */
    public URI uri(String group) {
        if (group == null) {
            return URI.create(URL + hookCommon);
        }
        String ticket;
        switch (group) {
            case "logNotify":
                ticket = hookLogNotify;
                break;
            default:
                ticket = hookCommon;
        }
        return URI.create(URL + ticket);
    }

    /**
     * 发送富文本消息
     *
     * @param group 发送到哪个群
     */
    public void send(String group, FeishuMessage feishuMessage) {
        try {
            feishuProxy.send(uri(group), FeishuSendRequest.build(feishuMessage));
        } catch (Exception e) {
            LogUtil.warn("1、飞书消息通知失败:{0}", e);
        }
    }

    /**
     * 发送卡片消息
     *
     * @param group 发送到哪个群
     */
    public void send(String group, FeishuCard feishuCard) {
        try {
            feishuProxy.send(uri(group), FeishuSendRequest.build(feishuCard));
        } catch (Exception e) {
            LogUtil.warn("2、飞书消息通知失败:{0}", e);
        }
    }

    /**
     * actionCard模式，不支持艾特
     */
    public void send(FeishuMessageRequest request) {
        FeishuCard card = new FeishuCard(request.getTitle());
        // 消息内容
        StringBuilder sb = new StringBuilder();
        for (FeishuMessageRequest.KeyValue keyValue : request.getMsg()) {
            if (keyValue.getKey() == null) {
                sb.append("\n");
            } else {
                sb.append(String.format("**%s：**%s\n", keyValue.getKey(), keyValue.getValue()));
            }
        }
        card.appendDivText(sb.toString());

        // 消息等级
        MessageLevelEnum level = request.getLevel();
        if (level != null) {
            FeishuCard.Header header = card.getHeader();
            switch (level) {
                case INFO:
                    header.info();
                    break;
                case SUCCESS:
                    header.success();
                    break;
                case WARN:
                    header.warn();
                    break;
                case ERROR:
                    header.error();
                    break;
                case SPECIAL:
                    header.special();
                    break;
                default:
            }
        }
        send(request.getGroup(), card);
    }

}
