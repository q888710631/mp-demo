package com.mp.model;

import com.baomidou.mybatisplus.annotation.*;
import com.mp.model.base.BaseEntity;

@TableName("role")
@InterceptorIgnore(tenantLine = "true")
public class Role extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("role_name")
    private String roleName;

    @TableField("role_key")
    private String roleKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }
}
