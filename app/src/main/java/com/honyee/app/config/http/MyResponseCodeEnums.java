package com.honyee.app.config.http;

/**
 * 自定义响应码
 */
public enum MyResponseCodeEnums {
    OK(200, "成功"),
    NOT_AUTH(401, "无权限访问"),
    COMMON_EXCEPTION(500, "请求失败"),
    PARAM_VALID_FAILURE(501, "参数错误"),
    ;

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
