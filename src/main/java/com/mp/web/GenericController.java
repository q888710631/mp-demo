package com.mp.web;

import com.mp.config.MyResponse;
import com.mp.config.jwt.JwtConstants;
import com.mp.config.jwt.JwtFilter;
import com.mp.config.jwt.LoginTypeEnum;
import com.mp.config.jwt.TokenProvider;
import com.mp.config.jwt.applet.AppletAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GenericController {
    private final Logger log = LoggerFactory.getLogger(GenericController.class);

    @GetMapping("/login")
    public MyResponse<?> login() {
        AppletAuthenticationToken authenticationToken = new AppletAuthenticationToken(Collections.emptyList(), 1L);
        String jwt = TokenProvider.createToken(
            authenticationToken,
            false,
            LoginTypeEnum.APPLET.toString(),
            authenticationToken.getAccountId(),
            data -> {
                if ("dev".equals("环境")) {
                    return data.claim("a", "b");
                }
                return data;
            }
        );
        Map<String, String> map = new HashMap<>();
        map.put(JwtConstants.AUTHORIZATION_HEADER, JwtConstants.BEARER + " " + jwt);
        return MyResponse.ok(map);
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody Object obj) {
        return ResponseEntity.ok("成功");
    }

}
