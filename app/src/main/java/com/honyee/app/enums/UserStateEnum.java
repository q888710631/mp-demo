package com.honyee.app.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 用户状态
 */
public enum UserStateEnum {
    DELETE(-1, "注销"),
    DISABLE(0, "禁用"),
    ENABLE(1, "启用"),

    ;

    @EnumValue
    private final int code;

    private final String descript;

    UserStateEnum(int code, String descript) {
        this.code = code;
        this.descript = descript;
    }

    public int getCode() {
        return code;
    }

    public String getDescript() {
        return descript;
    }
}
