package com.honyee.app.utils;

import com.honyee.app.config.mybatis.MybatisPlusTenantHandler;

/**
 * 获取租户ID
 */
public class TenantHelper {
    public static Long getTenantId() {
        return MybatisPlusTenantHandler.getTenantValue();
    }
}
