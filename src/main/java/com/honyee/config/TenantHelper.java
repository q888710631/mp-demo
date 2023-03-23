package com.honyee.config;

import com.honyee.config.mybatis.MybatisPlusTenantHandler;

/**
 * 获取租户ID
 */
public class TenantHelper {
    public static Long getTenantId() {
        return MybatisPlusTenantHandler.getTenantValue();
    }
}
