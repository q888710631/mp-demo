package com.mp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "角色DTO")
public class RoleDTO {
    @Schema(title = "角色名称")
    private String roleName;

    @Schema(title = "角色KEY")
    private String roleKey;

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
