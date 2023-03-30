package com.honyee.app.proxy.feishu;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

@FeignClient(name = "feishu", url = "https://open.feishu.cn")
public interface FeishuProxy {
    @PostMapping
    Object send(URI uri, @RequestBody FeishuSendRequest dto);
}
