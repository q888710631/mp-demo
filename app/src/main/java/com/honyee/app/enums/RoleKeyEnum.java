package com.honyee.app.enums;

public enum RoleKeyEnum {

    ROOT(0,"root","超管"),
    ADMIN(1,"admin","管理员"),
    TENANT(1,"tenant","租户"),
    CLIENT(3,"client", "用户")
    ;

    /**
     * 角色级别，例如 admin>client
     */
    private final int roleLevel;
    private final String roleKey;
    private final String descript;

    RoleKeyEnum(int roleLevel, String roleKey, String descript) {
        this.roleLevel = roleLevel;
        this.roleKey = roleKey;
        this.descript = descript;
    }

    public int getRoleLevel() {
        return roleLevel;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public String getDescript() {
        return descript;
    }

}
