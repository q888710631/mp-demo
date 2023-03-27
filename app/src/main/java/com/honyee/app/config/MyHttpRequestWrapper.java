package com.honyee.app.config;

import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * HttpServletRequest 装饰，使InputStream可以重复读取
 */
public class MyHttpRequestWrapper extends HttpServletRequestWrapper {
    byte[] body;
    String bodyStr;

    public MyHttpRequestWrapper(HttpServletRequest request) {
        super(request);
        this.body = readBody(request);
        this.bodyStr = new String(body, StandardCharsets.UTF_8);
     }

    public byte[] readBody(ServletRequest request) {
        try (InputStream inputStream = request.getInputStream()) {
            if (inputStream.available() != 0) {
                return StreamUtils.copyToByteArray(inputStream);
            }
        } catch (IOException e) {
             throw new RuntimeException(e);
        }
        return new byte[0];
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }
            @Override
            public boolean isReady() {
                return false;
            }
            @Override
            public void setReadListener(ReadListener readListener) {
            }
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };

    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyStr() {
        return bodyStr;
    }
}
