package com.mp.config;

import com.mp.config.mybatis.MybatisPlusTenantHandler;

/**
 * 获取租户ID
 */
public class TenantHelper {
    public static Long getTenantId() {
        return MybatisPlusTenantHandler.getTenantValue();
    }
}
