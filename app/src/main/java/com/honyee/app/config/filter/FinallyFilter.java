package com.honyee.app.config.filter;

import com.honyee.app.config.mybatis.MybatisPlusTenantHandler;

import javax.servlet.*;
import java.io.IOException;

public class FinallyFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            MybatisPlusTenantHandler.removeTenantValue();
        }
    }
}
