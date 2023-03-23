package com.honyee.proxy.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class HeaderInterceptor implements RequestInterceptor, EnvironmentAware {

    // 环境变量
    private Environment environment;

    @Override
    public void apply(RequestTemplate template) {
        byte[] body = template.body();
        if (body != null) {
            // ...
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
