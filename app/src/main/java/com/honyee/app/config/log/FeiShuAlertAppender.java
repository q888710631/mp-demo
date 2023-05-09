package com.honyee.app.config.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.honyee.app.AppApplication;
import com.honyee.app.config.Constants;
import com.honyee.app.config.nacos.NacosConfiguration;
import com.honyee.app.config.nacos.NacosCustomProperties;
import com.honyee.app.proxy.feishu.FeishuMessageRequest;
import com.honyee.app.service.FeishuService;
import com.honyee.app.utils.DateUtil;
import com.honyee.app.utils.LogUtil;
import com.honyee.app.utils.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class FeiShuAlertAppender extends AppenderBase<ILoggingEvent> {

    private final ObjectMapper objectMapper;

    private final FeishuService feishuService;

    private final String applicationName;

    private final String env;

    /**
     * 线程池
     */
    private final Executor executor;

    private final NacosConfiguration nacosConfiguration;


    public FeiShuAlertAppender(BeanFactory beanFactory,
                               ObjectMapper objectMapper,
                               FeishuService feishuService,
                               NacosConfiguration nacosConfiguration,
                               String applicationName,
                               String env) {
        this.objectMapper = objectMapper;
        this.feishuService = feishuService;
        this.nacosConfiguration = nacosConfiguration;
        this.applicationName = applicationName;
        this.env = env;

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1); //核心线程数
        executor.setMaxPoolSize(1);  //最大线程数
        executor.setQueueCapacity(1000); //队列大小
        executor.setKeepAliveSeconds(300); //线程最大空闲时间
        executor.setThreadNamePrefix("my-feishu-Executor-"); // 指定用于新创建的线程名称的前缀
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy()); // 丢弃策略（一共四种，此处省略）
        executor.initialize();
        // sleuth包装自定义线程池：自定义的异步任务线程池，会导致Sleuth无法新创建一个Span，需要通过LazyTraceExecutor包裹一层
        this.executor = new LazyTraceExecutor(beanFactory, executor);
    }

    @Override
    protected void append(ILoggingEvent event) {
        // 避免死循环
        if (FeishuService.class.getName().equals(event.getLoggerName())) {
            return;
        }

        boolean isErrorLog = Objects.equals(Level.ERROR, event.getLevel());
        boolean isWarnLog = Objects.equals(Level.WARN, event.getLevel());
        if (!isErrorLog && !isWarnLog) {
            return;
        }
        String loggerName = event.getLoggerName();
        if (StringUtils.isBlank(loggerName) || !loggerName.startsWith(Constants.BASE_PACKAGE)) {
            return;
        }

        String formattedMessage = event.getFormattedMessage();

        // 过滤不通知的信息
        if (formattedMessage != null && this.nacosConfiguration != null) {
            NacosCustomProperties customProperties = this.nacosConfiguration.getCustomProperties();
            if (customProperties != null) {
                List<String> feishuLogFilter = customProperties.getFeishuLogFilter();
                if (feishuLogFilter != null) {
                    if (feishuLogFilter.stream().anyMatch(formattedMessage::contains)) {
                        return;
                    }
                }
            }
        }

        Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
        FeishuMessageRequest feishuMessageRequest = new FeishuMessageRequest();
        if (isErrorLog) {
            feishuMessageRequest.error();
            feishuMessageRequest.setTitle("异常告警-ERROR");
        } else if (isWarnLog) {
            feishuMessageRequest.warn();
            feishuMessageRequest.setTitle("异常告警-WARN");
        }
        feishuMessageRequest.addMsg("告警环境", env);
        feishuMessageRequest.addMsg("应用名称", applicationName);
        feishuMessageRequest.addMsg("出现时间", DateUtil.COMMON_DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        String traceId = mdcPropertyMap.get("traceId");
        if (traceId != null) {
            feishuMessageRequest.addMsg("traceId", traceId);
            feishuMessageRequest.addMsg("spanId ", mdcPropertyMap.get("spanId"));
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            feishuMessageRequest.addMsg("Request URI", request.getRequestURI());
            feishuMessageRequest.addMsg("Request Method", request.getMethod());
            try {
                String param = objectMapper.writeValueAsString(request.getParameterMap());
                feishuMessageRequest.addMsg("Request Param", param);
                ServletInputStream inputStream = request.getInputStream();
                String body = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining(System.lineSeparator()));
                feishuMessageRequest.addMsg("Request Body", body);
            } catch (IOException e) {
                LogUtil.info("FeiShuAlertAppender异常", e);
            }
        }

        feishuMessageRequest.addMsg("错误信息", formattedMessage);
        executor.execute(() -> feishuService.send(feishuMessageRequest));
    }

}
