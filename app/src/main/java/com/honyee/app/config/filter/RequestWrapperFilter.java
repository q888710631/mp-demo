package com.honyee.app.config.filter;

import com.honyee.app.config.http.MyHttpRequestWrapper;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequestWrapperFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 装饰Request，用来反复获取body
//        chain.doFilter(new MyHttpRequestWrapper((HttpServletRequest) request), response);
        chain.doFilter(new ContentCachingRequestWrapper((HttpServletRequest) request), response);
    }
}
