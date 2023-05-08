package com.honyee.app.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.config.http.MyResponse;
import com.honyee.app.config.limit.RateLimit;
import com.honyee.app.dto.TestDTO;
import com.honyee.app.service.CacheService;
import com.honyee.app.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@Profile("dev")
public class TestController {

    @Resource
    private CacheService cacheService;

    @Resource
    private TestService testService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public void testGet(HttpServletResponse response) throws IOException {

        // 测试缓存
        TestDTO testDTO = cacheService.cacheRedis("123");
        cacheService.cacheMemory("123");
        String sssss = objectMapper.writeValueAsString(testDTO);
        TestDTO testDTO1 = objectMapper.readValue(sssss, TestDTO.class);

        // 测试输出流
        PrintWriter writer = response.getWriter();
        writer.write("close");
        writer.flush();
        writer.close();
        System.out.println("结束");
    }

    @PostMapping
    public ResponseEntity<?> test(@RequestBody(required = false) Object obj) {
        testService.lockTest("honyee");
        return ResponseEntity.ok(obj == null ? new HashMap<>() : obj);
    }

    @GetMapping("cache")
    public MyResponse<?> cacheTest() {
        testService.cacheTest();
        return MyResponse.ok();
    }

    @GetMapping("evict")
    public MyResponse<?> evictTest(@RequestParam(required = false) Map<String, Object> param) throws IllegalArgumentException{
        testService.evictTest();
        return MyResponse.ok();
    }

}
