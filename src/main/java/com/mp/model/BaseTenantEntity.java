package com.mp.model;

import com.baomidou.mybatisplus.annotation.TableField;

public class BaseTenantEntity extends BaseEntity{

    @TableField("tenant_id")
    protected Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
