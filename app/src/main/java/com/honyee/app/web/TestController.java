package com.honyee.app.web;

import com.honyee.app.config.Constants;
import com.honyee.app.config.http.MyResponse;
import com.honyee.app.config.http.TaskContextDecorator;
import com.honyee.app.config.limit.RateLimit;
import com.honyee.app.exp.CommonException;
import com.honyee.app.service.CacheService;
import com.honyee.app.service.TestService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("cache")
    public MyResponse<?> cacheTest() {
        testService.cacheTest();
        return MyResponse.ok();
    }

    @GetMapping("evict")
    public MyResponse<?> evictTest(@RequestParam(required = false) Map<String,Object> param) {
//        Runnable runnable = new TaskContextDecorator().decorate(() -> {
//            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//            if (requestAttributes != null) {
//                ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
//                HttpServletRequest request = attributes.getRequest();
//                Object traceIdObj = request.getAttribute(Constants.TRACE_ID);
//                System.out.println();
//            }
//
//            System.out.println();
//        });
//        new Thread(runnable).start();

//        testService.evictTest();
//        return MyResponse.ok();
//        throw new CommonException("故意的");
        throw new IndexOutOfBoundsException("特地的");
    }

    public static void main(String[] args) {
        List<Integer> list = List.of(1, 2, 3);
        List<Integer> integers = list.subList(0, 1);
        System.out.println();

    }


}
