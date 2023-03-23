package com.honyee.proxy.config;

import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

public class GenericConfig {
    private final static Logger log = LoggerFactory.getLogger(GenericConfig.class);

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
