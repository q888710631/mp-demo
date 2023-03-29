package com.honyee.app.exp;

/**
 * 异常：请求频率限制
 */
public class RateLimitExistsException extends CommonException {

    public RateLimitExistsException() {
        super("限流");
    }

    public RateLimitExistsException(Object obj) {
        super("限流：" + obj);
    }

    @Override
    public String getCommonMessage() {
        return "接口拥堵，稍后再试";
    }
}
