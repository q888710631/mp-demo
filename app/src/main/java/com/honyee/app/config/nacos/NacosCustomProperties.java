package com.honyee.app.config.nacos;

import java.util.List;

public class NacosCustomProperties {
    /**
     * 不输出的日志 contain
     */
    private List<String> feishuLogFilter;

    public List<String> getFeishuLogFilter() {
        return feishuLogFilter;
    }

    public void setFeishuLogFilter(List<String> feishuLogFilter) {
        this.feishuLogFilter = feishuLogFilter;
    }
}
