package com.mp.web;

import com.mp.config.TenantHelper;
import com.mp.exp.CommonException;
import com.mp.exp.DataNotExistsException;
import com.mp.service.CacheService;
import com.mp.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(GenericController.class);

    @Resource
    private CacheService cacheService;

    @Resource
    private GenericService genericService;

    @GetMapping("/test")
    public void testGet(String abc, HttpServletResponse response) throws IOException {
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
        throw new DataNotExistsException();
//        return ResponseEntity.ok(obj);
    }

    /**
     * 当前用户可用菜单
     */
    @GetMapping("/user-menu")
    public List<String> userMenu() {
        return genericService.userMenu(TenantHelper.getTenantId());
    }

}
