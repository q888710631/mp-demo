package com.honyee.app.config.nacos;

import com.alibaba.nacos.api.config.listener.Listener;

import java.util.concurrent.Executor;

public class NacosListener implements Listener {

    private final NacosConfiguration nacosConfiguration;

    public NacosListener(NacosConfiguration nacosConfiguration) {
        this.nacosConfiguration = nacosConfiguration;
    }


    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void receiveConfigInfo(String configInfo) {
        boolean result = nacosConfiguration.analysisConfig(configInfo);
    }
}