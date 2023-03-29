package com.honyee.app.exp;

/**
 * 异常：请求锁超时
 */
public class LockOutOfTimeException extends CommonException {

    public LockOutOfTimeException() {
        super("请求锁超时");
    }
    public LockOutOfTimeException(Object obj) {
        super("请求锁超时：" + obj);
    }

    @Override
    public String getCommonMessage() {
        return "系统繁忙，稍后重试";
    }
}
