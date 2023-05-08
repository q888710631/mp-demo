package com.honyee.app.exp;

/**
 * 异常：请求频率限制
 */
public class RateLimitException extends CommonException {

    public RateLimitException() {
        super("限流");
    }

    public RateLimitException(Object obj) {
        super("限流：" + obj);
    }

    @Override
    public String getCommonMessage() {
        return "接口拥堵，稍后再试";
    }
}
