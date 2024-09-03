package com.honyee.app.web;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.honyee.app.config.RedisDelayConfig;
import com.honyee.app.config.http.MyResponse;
import com.honyee.app.mapper.PersonMapper;
import com.honyee.app.service.CacheService;
import com.honyee.app.service.TestService;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Autowired
    private PersonMapper personMapper;
    
    @Autowired
    RedisDelayConfig redisDelayConfig;

    static interface Base{
        Integer getP();

    }

    @Data
    static class Parent implements Base{
        @NotNull
        Integer p;
    }
    @Data
    static class Child extends Parent {
        @NotNull
        Integer n;
    }

    @PostMapping("valid")
    public Object valid(@Valid @RequestBody Child dto) {
        return dto;
    }

    @GetMapping("/query/{orderNo:\\d+}")
    public void testGet(@PathVariable String orderNo, HttpServletResponse response) throws IOException, InterruptedException {
        // 测试输出流
        PrintWriter writer = response.getWriter();
        writer.write("close");
        writer.flush();
        writer.close();
        System.out.println("结束");
    }

    @Autowired
    private FileController fileController;

    @PostMapping
    public ResponseEntity<?> test() throws JsonProcessingException {
        redisDelayConfig.produce();
        return ResponseEntity.ok(null);
    }


    private final ExecutorService nonBlockingService = Executors.newSingleThreadExecutor();

    /*
        // 前端
        const eventSource = new EventSource('http://127.0.0.1:1222/api/test/sse');
			eventSource.onmessage = ({ data }) => {
			console.log('New message', JSON.parse(data));
		};
     */
    @CrossOrigin(origins = "*")
    @GetMapping("/sse")
    public SseEmitter handle(SseEmitter emitter) {
        for (int i = 0; i < 5; i++) {
            final int a = i;
            nonBlockingService.submit(() -> {
                try {
                    String message = objectMapper.writeValueAsString(Map.of("a", "123"));
                    emitter.send(message);
                    Thread.sleep(1000);
                    if (a == 4) {
                        // complete之后，前端会重新发起一次请求
                        emitter.complete();
                    }
                } catch (IOException | InterruptedException e) {
                    emitter.completeWithError(e);
                }
            });

        }
        
        return emitter;
    }


    @GetMapping("cache")
    public MyResponse<?> cacheTest() {
        testService.cacheTest();
        return MyResponse.ok();
    }

    @GetMapping("evict")
    public MyResponse<?> evictTest(@RequestParam(required = false) Map<String, Object> param) throws IllegalArgumentException {
        testService.evictTest();
        return MyResponse.ok();
    }

    /**
     * 导出excel
     */
    @GetMapping("/excel/download")
    public void excelDownload(HttpServletResponse response) throws IOException {
        testService.excelDownload(response);
    }

}
