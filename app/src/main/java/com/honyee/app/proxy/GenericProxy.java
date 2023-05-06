package com.honyee.app.proxy;

import com.honyee.app.proxy.config.GenericConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "generic", url = "https://www.baidu.com", configuration = GenericConfig.class)
public interface GenericProxy {

}
