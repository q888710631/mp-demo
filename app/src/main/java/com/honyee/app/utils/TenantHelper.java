package com.honyee.app.utils;

import com.honyee.app.config.mybatis.MybatisPlusTenantHandler;

/**
 * 获取租户ID
 */
public class TenantHelper {
    public static Long getCurrentUserId() {
        return MybatisPlusTenantHandler.getTenantValue();
    }

    /**
     * 关闭租户注入
     */
    public static void disableTenant() {
        MybatisPlusTenantHandler.setTenantEnable(Boolean.FALSE);
    }

    /**
     * 开启租户注入
     */
    public static void enableTenant() {
        MybatisPlusTenantHandler.setTenantEnable(Boolean.TRUE);
    }


}
