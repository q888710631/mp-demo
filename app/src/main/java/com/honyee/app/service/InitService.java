package com.honyee.app.service;

import com.honyee.app.utils.SpringUtil;
import org.apache.ibatis.builder.StaticSqlSource;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class InitService implements ApplicationContextAware, ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.CONTEXT = applicationContext;

    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        StaticSqlSource.initComplete = true;
    }
}
