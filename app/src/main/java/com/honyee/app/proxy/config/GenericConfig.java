package com.honyee.app.proxy.config;

import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class GenericConfig {

    @Bean
    public HeaderInterceptor headerInterceptor() {
        return new HeaderInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                super.apply(template);
                // ...
            }
        };
    }
}
