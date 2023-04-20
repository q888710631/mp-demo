package com.honyee.app.config.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.honyee.app.config.Constants;
import com.honyee.app.proxy.feishu.FeishuMessageRequest;
import com.honyee.app.service.FeishuService;
import com.honyee.app.utils.DateUtil;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class FeiShuAlertAppender extends AppenderBase<ILoggingEvent> {

    private final FeishuService feishuService;

    private final String applicationName;

    private final String env;


    public FeiShuAlertAppender(FeishuService feishuService, String applicationName, String env) {
        this.feishuService = feishuService;
        this.applicationName = applicationName;
        this.env = env;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!Objects.equals(Level.ERROR, event.getLevel())) {
            return;
        }
        String loggerName = event.getLoggerName();
        if (!loggerName.startsWith(Constants.BASE_PACKAGE)) {
            return;
        }
        String formattedMessage = event.getFormattedMessage();
        Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
        FeishuMessageRequest request = new FeishuMessageRequest();
        request.error();
        request.setTitle("异常告警");
        request.addMsg("告警环境", env);
        request.addMsg("应用名称", applicationName);
        request.addMsg("出现时间", DateUtil.COMMON_DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        request.addMsg("traceId", mdcPropertyMap.get("traceId"));
        request.addMsg("spanId", mdcPropertyMap.get("spanId"));
        request.addMsg("错误信息", formattedMessage);

        feishuService.send(request);
    }

}
