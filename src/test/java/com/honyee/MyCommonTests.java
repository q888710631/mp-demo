package com.honyee;

import com.honyee.config.jwt.JwtConstants;
import com.honyee.config.jwt.LoginTypeEnum;
import com.honyee.config.jwt.TokenProvider;
import com.honyee.config.jwt.my.MyAuthenticationToken;
import com.honyee.dto.ParamDTO;
import com.honyee.utils.HttpUtil;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyCommonTests {
    private static final Logger log = LoggerFactory.getLogger(MyCommonTests.class);

    @Test
    public void useToken() {
        MyAuthenticationToken authenticationToken = new MyAuthenticationToken(1L, Collections.emptyList());
        String jwt = TokenProvider.createToken(
            authenticationToken,
            false,
            LoginTypeEnum.COMMON.toString(),
            authenticationToken.getUserId(),
            data -> {
                if ("dev".equals("环境")) {
                    return data.claim("a", "b");
                }
                return data;
            }
        );
        Map<String, String> map = new HashMap<>();
        map.put(JwtConstants.AUTHORIZATION_HEADER, JwtConstants.BEARER + " " + jwt);

    }

    @Test
    public void urlToDTO() throws ReflectiveOperationException {
        String url = "https://www.honyee.com/html/honyee.html?id=123&id=456&type=honyee&data.name=honyee";
        ParamDTO paramDTO = HttpUtil.readQuery(url, ParamDTO.class);
        log.info(() -> "urlToDTO");
    }
}
