package com.honyee.app.config.feign;

import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"com.honyee.app.proxy"})
public class FeignConfiguration {
    @Bean
    public Logger logger() {
        return new FeignLogger();
    }
}
