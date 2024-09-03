package com.honyee.app.model.base;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;

@InterceptorIgnore(tenantLine = "false")
public class BaseTenantEntity extends BaseEntity {

    @Schema(title = "租户ID")
    @TableField("tenant_id")
    protected Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
