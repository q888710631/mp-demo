package com.honyee.app.model;

import com.baomidou.mybatisplus.annotation.*;
import com.honyee.app.model.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "角色")
@TableName("role")
@InterceptorIgnore(tenantLine = "true")
public class Role extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(title = "角色级别")
    @TableField("role_level")
    private Integer roleLevel;

    @Schema(title = "角色名称")
    @TableField("role_name")
    private String roleName;

    @Schema(title = "角色KEY")
    @TableField("role_key")
    private String roleKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(Integer roleLevel) {
        this.roleLevel = roleLevel;
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
