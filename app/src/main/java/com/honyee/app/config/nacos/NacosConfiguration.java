package com.honyee.app.config.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.notify.NotifyCenter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.honyee.app.utils.LogUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@EnableDiscoveryClient
@Configuration
public class NacosConfiguration implements InitializingBean, DisposableBean {
    @Value("${spring.cloud.nacos.discovery.enabled}")
    private Boolean enable;

    @Value("${spring.application.name}")
    private String applicationName;

    @Resource
    private NacosConfigManager nacosConfigManager;

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private NacosConfigProperties nacosConfigProperties;

    // 线程，仅在spring.cloud.nacos.discovery.enabled=true时有效
    private ScheduledExecutorService executor = null;

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Boolean.TRUE.equals(enable)) {
            this.executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("my-nacos-discover-%d").build());
            init();

            // 订阅
            NotifyCenter.registerSubscriber(new NacosInstancesChangeNotifier());
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
                nacosConfigManager.getConfigService().addListener(getDataId(), nacosConfigProperties.getGroup(), new NacosListener());
                LogUtil.info("### nacos add listener 执行成功");
            } catch (NacosException e) {
                LogUtil.error("### nacos add listener 执行失败：{}", e.getMessage());
            }
        } else {
            // 执行失败，延迟重试
            LogUtil.error("### addAuthenticateListener 执行失败，稍后重试");
            executor.schedule(this::init, 5L, TimeUnit.SECONDS);
        }

    }

    private boolean loadConfig(String dataId) {
        try {
            String configInfo = nacosConfigManager.getConfigService().getConfig(dataId, nacosConfigProperties.getGroup(), nacosConfigProperties.getTimeout());
            LogUtil.info("nacos配置获取：{}", configInfo);
            return analysisConfig(configInfo);
        } catch (NacosException e) {
            LogUtil.error("nacos配置获取失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 解析配置
     * @param configInfo yaml配置信息
     */
    private boolean analysisConfig(String configInfo) {
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(configInfo, Map.class);
        return true;
    }

    private String getDataId() {
        return this.applicationName;
    }

    public class NacosListener implements Listener {

        @Override
        public Executor getExecutor() {
            return null;
        }

        @Override
        public void receiveConfigInfo(String configInfo) {
            boolean result = analysisConfig(configInfo);
        }
    }

}
