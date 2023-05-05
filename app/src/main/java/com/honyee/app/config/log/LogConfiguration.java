package com.honyee.app.config.log;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.service.FeishuService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfiguration {
    public LogConfiguration(
        @Value("${spring.application.name}") String appName,
        @Value("${spring.profiles.active}") String env,
        @Value("${application.feishu.enable.log-notify}") Boolean enableLogNotify,
        BeanFactory beanFactory,
        ObjectMapper objectMapper,
        FeishuService feishuService
    ) {
        if (Boolean.TRUE.equals(enableLogNotify)) {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            FeiShuAlertAppender appender = new FeiShuAlertAppender(beanFactory, objectMapper, feishuService, appName, env);
            appender.setContext(context);
            appender.setName(appName);
            appender.start();

            context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).addAppender(appender);
        }

    }
}
