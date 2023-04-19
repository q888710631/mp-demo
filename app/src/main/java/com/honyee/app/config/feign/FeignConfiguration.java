package com.honyee.app.config.feign;

import com.honyee.app.config.Constants;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {Constants.PROXY_PACKAGE})
public class FeignConfiguration {
    @Bean
    public Logger logger() {
        return new FeignLogger();
    }

    /**
     * feign全局拦截器
     */
    @Bean
    public RequestInterceptor feignRequestInterceptor(Tracer tracer) {
        return template -> {
            // 请求头携带traceId
            String traceId = "";
            Span span = tracer.currentSpan();
            if (span != null) {
                traceId = span.context().traceId();
            }
            template.header(Constants.TRACE_ID, traceId);
        };
    }
}
