package com.mp.web;

import com.mp.service.CacheService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
@RequestMapping("/api/test")
@Profile("dev")
public class TestController {

    @Resource
    private CacheService cacheService;

    @GetMapping
    public void testGet( HttpServletResponse response) throws IOException {
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

    @PostMapping
    public ResponseEntity<?> test(@RequestBody Object obj) {
        return ResponseEntity.ok(obj);
    }

}
