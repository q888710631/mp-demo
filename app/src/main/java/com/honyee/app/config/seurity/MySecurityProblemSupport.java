package com.honyee.app.config.seurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.config.http.MyResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MySecurityProblemSupport implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public MySecurityProblemSupport(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException exception) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        MyResponse<?> myResponse = new MyResponse<>();
        myResponse.setCode(HttpStatus.UNAUTHORIZED.value());
        if (exception instanceof BadCredentialsException) {
            myResponse.setMessage("账号或密码错误");
        } else if (exception instanceof AccessForbiddenException) {
            myResponse.setCode(HttpStatus.FORBIDDEN.value());
            myResponse.setMessage("无权限访问");
        } else if (exception instanceof DisabledException) {
            myResponse.setCode(HttpStatus.FORBIDDEN.value());
            myResponse.setMessage("账号已被禁用");
        } else if (exception instanceof LockedException) {
            myResponse.setCode(HttpStatus.FORBIDDEN.value());
            myResponse.setMessage("账号已被锁定");
        } else {
            myResponse.setMessage(exception.getMessage());
        }
        objectMapper.writeValue(response.getOutputStream(), myResponse);
    }

}