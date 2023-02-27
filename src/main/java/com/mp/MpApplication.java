package com.mp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.mp.mapper.CityMapper;
import com.mp.model.City;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@MapperScan("com.mp.mapper")
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
