package com.mp.config.seurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mp.config.MyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MySecurityProblemSupport implements AuthenticationEntryPoint{

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException exception) throws IOException {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        if (exception instanceof AccessForbiddenException) {
            httpStatus = HttpStatus.FORBIDDEN;
        }
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());

        MyResponse<?> myResponse = new MyResponse<>();
        myResponse.setCode(httpStatus.value());
        myResponse.setMsg(exception.getMessage());
        objectMapper.writeValue(response.getOutputStream(), myResponse);
    }

}