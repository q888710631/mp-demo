package com.mp.proxy;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "generic", url = "https://www.baidu.com")
public interface GenericProxy {

}
