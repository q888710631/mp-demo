package com.honyee.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AppApplication {
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(AppApplication.class, args);
    }

    public static <T> T getBean(Class<T> var1) {
        return context.getBeanFactory().getBean(var1);
    }
}
