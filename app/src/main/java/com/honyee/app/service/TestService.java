package com.honyee.app.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.honyee.app.config.delay.DelayTaskConfiguration;
import com.honyee.app.config.lock.RedisLock;
import com.honyee.app.delay.MyDelayParam;
import com.honyee.app.dto.TestDTO;
import com.honyee.app.dto.excel.TestExcelDTO;
import com.honyee.app.utils.DateUtil;
import com.honyee.app.utils.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TestService {
    @Resource
    private CacheService cacheService;

    @RedisLock(value = "test", key = "#query")
    public String lockTest(String query) {
        log.info("TestService.test()............");
        MyDelayParam myDelayParam = new MyDelayParam();
        myDelayParam.setId(System.currentTimeMillis());
        myDelayParam.setTitle("myDelayParam");
        for (int i = 0; i < 10; i++) {
            DelayTaskConfiguration.submit(myDelayParam, i + 2, TimeUnit.SECONDS);
        }
        return "complete";
    }

    int i = 0;
    String[] arr = {"first", "twice", "third"};

    public void cacheTest() {
        long currentTimeMillis = System.currentTimeMillis();
        // 批量添加缓存
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < 3; j++) {
                cacheService.cacheTest(arr[i], currentTimeMillis + j + "");
            }
        }
    }

    public void evictTest() {
        i = (i + 1) % arr.length;
        cacheService.evictTest(arr[i]);
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<Object, Object> map = new HashMap<>();
        map.put("instant", Instant.now().toEpochMilli());
        String json = objectMapper.writeValueAsString(map);
        objectMapper.registerModule(new JavaTimeModule());
        TestDTO dto = objectMapper.readValue(json, TestDTO.class);
        System.out.println();
    }

    public void excelDownload(HttpServletResponse response) throws IOException {
        String fileName = "测试下载excel" + DateUtil.COMMON_DATE_TIME_FORMATTER.format(LocalDateTime.now());
        ExcelUtil.initResponse(response, fileName);
        ExcelWriter writer = null;
        try {
            writer = EasyExcel.write(response.getOutputStream()).build();
            writer.write(queryTestExcelData(), TestExcelDTO.SHEET);
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }
    }

    private Collection<TestExcelDTO> queryTestExcelData() {
        TestExcelDTO testExcelDTO = new TestExcelDTO();
        testExcelDTO.setContent1("1号1号1号1号");
        testExcelDTO.setContent2("2号2号2号2号2号");

        List<TestExcelDTO> list = new ArrayList<>();
        list.add(testExcelDTO);
        list.add(testExcelDTO);
        list.add(testExcelDTO);
        list.add(testExcelDTO);
        return list;
    }
}
