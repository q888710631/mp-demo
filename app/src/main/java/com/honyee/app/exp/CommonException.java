package com.honyee.app.exp;

public class CommonException extends RuntimeException {

    private final int code;

    public CommonException(String message) {
        super(message);
        this.code = 500;
    }

    public CommonException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public CommonException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public CommonException(Throwable cause) {
        super(cause);
        this.code = 500;
    }
}
