package com.mp.web;

import com.mp.config.MyResponse;
import com.mp.config.jwt.JwtConstants;
import com.mp.config.jwt.LoginTypeEnum;
import com.mp.config.jwt.TokenProvider;
import com.mp.config.jwt.my.MyAuthenticationToken;
import com.mp.dto.CitySimpleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GenericController {
    private final Logger log = LoggerFactory.getLogger(GenericController.class);

    @GetMapping("/login")
    public MyResponse<?> login() {
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
        return MyResponse.ok(map);
    }

    @GetMapping("/test")
    public void testGet(HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        writer.write("close");
        writer.flush();
        writer.close();
        System.out.println("最终");
    }

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody Object obj) {
        CitySimpleDTO citySimpleDTO = new CitySimpleDTO();
        citySimpleDTO.setName("abc");
        return ResponseEntity.ok(citySimpleDTO);
    }

}
