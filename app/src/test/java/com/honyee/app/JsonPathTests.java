package com.honyee.app;

import com.honyee.app.utils.JsonUtil;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

public class JsonPathTests {
    private static final Logger log = LoggerFactory.getLogger(JsonPathTests.class);


    @Test
    public void testJsonPath() {
        String[] paths = new String[]{"$.data.message", "$.data.logo", "$.data.title"
        };
        String source = "{\n" +
            "    \"data\": {\n" +
            "        \"message\": \"新\",\n" +
            "        \"logo\": \"新\",\n" +
            "        \"title\": \"新\"\n" +
            "    }\n" +
            "}";
        String target = "{\n" +
            "    \"data\": {\n" +
            "        \"message\": \"旧\",\n" +
//            "        \"logo\": \"旧\",\n" +
            "        \"name\": \"旧\",\n" +
            "        \"title\": \"旧\"\n" +
            "    }\n" +
            "}";

        // 合并指定项页面设置
        String result = JsonUtil.merge(source, target, paths);
        log.info(() -> "source:\n" + source);
        log.info(() -> "target:\n" + target);
        log.info(() -> "result:\n" + result);
    }
}
