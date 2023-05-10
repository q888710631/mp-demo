package com.honyee.app.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 住院状态
 */
public enum InPatientStateEnum {
    ALL(0, "所有"),
    IN(1, "出院"),
    OUT(2, "出院"),

    ;

    @EnumValue
    private final int code;

    private final String descript;

    InPatientStateEnum(int code, String descript) {
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
