package com.honyee.app.exp;

/**
 * 异常：请求锁超时
 */
public class LockOutTimeException extends CommonException {

    public LockOutTimeException() {
        super("请求锁超时");
    }
    public LockOutTimeException(Object obj) {
        super("请求锁超时：" + obj);
    }

}
