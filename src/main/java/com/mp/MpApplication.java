package com.mp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

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
