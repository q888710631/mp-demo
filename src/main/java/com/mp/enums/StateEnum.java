package com.mp.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum StateEnum {
    COMMIT("c"),
    SUCCESS("s"),
    FAILURE("f")
    ;

    @EnumValue
    private final String value;

    StateEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
