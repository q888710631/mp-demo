package com.mp.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

public class MybatisPlusTenantHandler implements TenantLineHandler {

    private static final ThreadLocal<Long> TENANT_LOCAL = new ThreadLocal<>();

    public static void setTenantValue(Long tenantId) {
        TENANT_LOCAL.set(tenantId);
    }

    public static Long getTenantValue() {
        return TENANT_LOCAL.get();
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
        return MybatisPlusTenantLineInnerInterceptor.HAS_IGNORE_ANN_TABLE_NAME.contains(tableName.toLowerCase());
    }
}
