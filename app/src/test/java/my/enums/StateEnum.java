package my.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum StateEnum {
    COMMIT("commit"),
    SUCCESS("success"),
    FAILURE("failure")
    ;

    /**
     * 当实体类的属性是普通枚举，且是其中一个字段，使用该注解来标注枚举类里的那个属性对应字段
     */
    @EnumValue
    private final String value;

    StateEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
