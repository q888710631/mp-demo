package com.honyee.app.proxy;

import com.honyee.app.dto.TestDTO;
import com.honyee.app.proxy.config.GenericConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "genericcom.honyee.proxy", url = "https://wanju.12301dev.com", configuration = GenericConfig.class)
public interface GenericProxy {

    @GetMapping("/api/app/test")
    ResponseEntity<Object> test(@RequestParam("a") String a);

    @GetMapping("/api/app/test")
    ResponseEntity<Object> test2(@SpringQueryMap TestDTO dto);


}
