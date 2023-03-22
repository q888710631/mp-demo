package com.mp.web;

import com.mp.config.TenantHelper;
import com.mp.service.CacheService;
import com.mp.service.GenericService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GenericController {

    @Resource
    private CacheService cacheService;

    @Resource
    private GenericService genericService;

    @GetMapping("/test")
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

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody Object obj) {
        return ResponseEntity.ok(obj);
    }

    /**
     * 当前用户可用菜单
     */
    @GetMapping("/user-menu")
    public List<String> userMenu() {
        return genericService.userMenu(TenantHelper.getTenantId());
    }

}
