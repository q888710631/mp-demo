package com.honyee.app.config.log;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.service.FeishuService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfiguration implements ApplicationListener<ApplicationReadyEvent> {
    private LoggerContext context;
    private FeiShuAlertAppender appender;

    public LogConfiguration(
        @Value("${spring.application.name}") String appName,
        @Value("${spring.profiles.active}") String env,
        @Value("${application.feishu.enable.log-notify}") Boolean enableLogNotify,
        BeanFactory beanFactory,
        ObjectMapper objectMapper,
        FeishuService feishuService
    ) {
        if (Boolean.TRUE.equals(enableLogNotify)) {
            this.context = (LoggerContext) LoggerFactory.getILoggerFactory();
            this.appender = new FeiShuAlertAppender(beanFactory, objectMapper, feishuService, appName, env);
            this.appender.setContext(context);
            this.appender.setName(appName);
            this.appender.start();
        }


    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (this.context != null && this.appender != null) {
            this.context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).addAppender(this.appender);
        }
    }
}
