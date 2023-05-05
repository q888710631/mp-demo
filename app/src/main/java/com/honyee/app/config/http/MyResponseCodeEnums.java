package com.honyee.app.config.http;

/**
 * 自定义响应码
 */
public enum MyResponseCodeEnums {
    OK(200, "success"),
    COMMON_EXCEPTION(500, "failure");

    private final int code;
    private final String message;

    MyResponseCodeEnums(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
