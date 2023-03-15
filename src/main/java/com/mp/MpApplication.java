package com.mp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@EnableCaching
@SpringBootApplication
public class MpApplication {

    public static ConfigurableApplicationContext CONTEXT;

    public static void main(String[] args) {
        CONTEXT = SpringApplication.run(MpApplication.class, args);
    }

    public static <T> T getBean(Class<T> var1) {
       return CONTEXT.getBeanFactory().getBean(var1);
    }

}
