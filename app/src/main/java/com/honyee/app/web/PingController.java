package com.honyee.app.web;

import com.honyee.app.config.http.MyResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/ping")
public class PingController {
    private final MyResponse<?> response = MyResponse.ok();
    @GetMapping
    public MyResponse<?> ping() {
        return response;
    }
}
