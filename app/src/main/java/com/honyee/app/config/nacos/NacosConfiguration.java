package com.honyee.app.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.naming.utils.NetUtils;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.honyee.app.utils.YamlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@EnableDiscoveryClient
@Configuration
public class NacosConfiguration implements InitializingBean, DisposableBean {
    @Value("${spring.cloud.nacos.discovery.enabled}")
    private Boolean enable;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port:8080}")
    private int port ;

    @Autowired(required = false)
    private NacosConfigManager nacosConfigManager;

    @Autowired(required = false)
    private DiscoveryClient discoveryClient;

    @Autowired(required = false)
    private NacosConfigProperties nacosConfigProperties;

    @Autowired(required = false)
    private NamingService namingService;

    private NacosInstancesChangeNotifier nacosInstancesChangeNotifier;

    private NacosCustomProperties customProperties = null;

    // 线程，仅在spring.cloud.nacos.discovery.enabled=true时有效
    private ScheduledExecutorService executor = null;

    @Override
    public void destroy() throws Exception {
        if (Boolean.TRUE.equals(enable)) {
            // 取消订阅：实例变动监听
            NotifyCenter.deregisterSubscriber(nacosInstancesChangeNotifier);
            String ip = NetUtils.localIP();
            // 移除自身实例
            namingService.deregisterInstance(applicationName, nacosConfigProperties.getGroup(), ip, port);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Boolean.TRUE.equals(enable)) {
            this.executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("my-nacos-discover-%d").build());
            init();
            this.nacosInstancesChangeNotifier = new NacosInstancesChangeNotifier();
            // 订阅
            NotifyCenter.registerSubscriber(nacosInstancesChangeNotifier);
        }
    }

    /**
     * 心跳
     */
    @EventListener
    public void onApplicationEvent(HeartbeatEvent event) {
        // ...
    }

    private void init() {
        if (loadConfig(getDataId())) {
            try {
                nacosConfigManager.getConfigService().addListener(getDataId(), nacosConfigProperties.getGroup(), new NacosListener(this));
                log.info("### nacos add listener 执行成功");
            } catch (NacosException e) {
                log.error("### nacos add listener 执行失败：{}", e.getMessage());
            }
        } else {
            // 执行失败，延迟重试
            log.error("### nacos load config 执行失败，稍后重试");
            executor.schedule(this::init, 5L, TimeUnit.SECONDS);
        }
    }

    public boolean loadConfig(String dataId) {
        try {
            String configInfo = nacosConfigManager.getConfigService().getConfig(dataId, nacosConfigProperties.getGroup(), nacosConfigProperties.getTimeout());
            log.info("nacos配置获取：{}", configInfo);
            return analysisConfig(configInfo);
        } catch (NacosException e) {
            log.error("nacos配置获取失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 解析配置
     * @param configInfo yaml配置信息
     */
    public boolean analysisConfig(String configInfo) {
        if (configInfo == null) {
            log.warn("nacos 没有读取到配置文件，可能未连接成功或没有该配置文件");
            return false;
        }
        try {
            this.customProperties = YamlUtil.loadAs(configInfo, NacosCustomProperties.class);
        } catch (Exception e) {
            log.error("加载nacos配置文件失败：" + e.getMessage());
            return false;
        }
        return true;
    }

    private String getDataId() {
        return this.applicationName;
    }

    public NacosCustomProperties getCustomProperties() {
        return customProperties;
    }
}
