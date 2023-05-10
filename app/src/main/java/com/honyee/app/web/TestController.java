package com.honyee.app.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.config.http.MyResponse;
import com.honyee.app.config.limit.RateLimit;
import com.honyee.app.dto.TestDTO;
import com.honyee.app.mapper.PersonMapper;
import com.honyee.app.model.Person;
import com.honyee.app.model.Role;
import com.honyee.app.model.User;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonMapper personMapper;

    @GetMapping
    public void testGet(HttpServletResponse response) throws IOException {
        Person person = new Person();
        person.setNickname(System.currentTimeMillis() + "");
        person.setRoleList(new ArrayList<>());
        Role role = new Role();
        role.setRoleKey(System.currentTimeMillis() + "");
        person.getRoleList().add(role);
        person.setRole(role);
        personMapper.insert(person);

        List<Person> list = personMapper.selectList(new QueryWrapper<>());
        List<Role> roleList = list.get(0).getRoleList();
        Role role1 = roleList.get(0);
        // 测试输出流
        PrintWriter writer = response.getWriter();
        writer.write("close");
        writer.flush();
        writer.close();
        System.out.println("结束");
    }

    @PostMapping
    public ResponseEntity<?> test(TestDTO dto,  @RequestBody(required = false) Object obj) throws JsonProcessingException {
//        testService.lockTest("honyee");
        // 测试缓存
//        cacheService.cacheMemory("123");
//        TestDTO testDTO = cacheService.cacheRedis("123");
//        String json = objectMapper.writeValueAsString(dto);
//        TestDTO testDTO1 = objectMapper.readValue(json, TestDTO.class);
        return ResponseEntity.ok(dto);
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
