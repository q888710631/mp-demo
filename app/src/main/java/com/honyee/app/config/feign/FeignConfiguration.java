package com.honyee.app.config.feign;

import com.honyee.app.config.Constants;
import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {Constants.PROXY_PACKAGE})
public class FeignConfiguration {
    @Bean
    public Logger logger() {
        return new FeignLogger();
    }
}
