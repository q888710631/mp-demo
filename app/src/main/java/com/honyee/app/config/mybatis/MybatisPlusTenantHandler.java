package com.honyee.app.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.honyee.app.config.Constants;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

public class MybatisPlusTenantHandler implements TenantLineHandler {

    private static final ThreadLocal<Long> TENANT_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> TENANT_ENABLE = new ThreadLocal<>();

    public static void setTenantValue(Long tenantId) {
        TENANT_LOCAL.set(tenantId);
    }

    public static void setTenantEnable(Boolean enable) {
        TENANT_ENABLE.set(enable);
    }

    /**
     * 租户ID
     */
    public static Long getTenantValue() {
        return TENANT_LOCAL.get();
    }

    /**
     * 是否开启租户注入，默认true
     */
    public static boolean getTenantEnable() {
        return !Boolean.FALSE.equals(TENANT_ENABLE.get());
    }

    public static void removeAll() {
        TENANT_LOCAL.remove();
        TENANT_ENABLE.remove();
    }

    /**
     * 获取租户ID 实际应该从用户信息中获取
     */
    @Override
    public Expression getTenantId() {
        Long value = TENANT_LOCAL.get();
        if (value != null) {
            return new LongValue(value);
        }
        return null;
    }

    /**
     * 获取租户表字段 默认为tenant_id
     */
    @Override
    public String getTenantIdColumn() {
        return Constants.COLUMN_TENANT_ID;
    }

    /**
     * 表过滤，返回true，表示当前表不进行租户过滤
     *
     * @param tableName 表名
     */
    @Override
    public boolean ignoreTable(String tableName) {
        return !getTenantEnable() || MybatisPlusTenantLineInnerInterceptor.IGNORE_TABLE_NAME.contains(tableName.toLowerCase());
    }
}
