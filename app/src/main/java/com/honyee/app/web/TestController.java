package com.honyee.app.web;

import com.honyee.app.config.limit.RateLimit;
import com.honyee.app.service.CacheService;
import com.honyee.app.service.TestService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@RestController
@RequestMapping("/api/test")
@Profile("dev")
public class TestController {

    @Resource
    private CacheService cacheService;

    @Resource
    private TestService testService;

    @GetMapping
    public void testGet(HttpServletResponse response) throws IOException {

        // 测试缓存
        cacheService.cacheRedis("123");
        cacheService.cacheMemory("123");

        // 测试输出流
        PrintWriter writer = response.getWriter();
        writer.write("close");
        writer.flush();
        writer.close();
        System.out.println("结束");
    }

    @RateLimit(limitIp = true)
    @PostMapping
    public ResponseEntity<?> test(@RequestBody(required = false) Object obj) {
        testService.lockTest("honyee");
        return ResponseEntity.ok(obj == null ? new HashMap<>() : obj);
    }

}
