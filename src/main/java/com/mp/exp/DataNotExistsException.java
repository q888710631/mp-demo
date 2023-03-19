package com.mp.exp;

/**
 * 异常：数据不存在
 */
public class DataNotExistsException extends CommonException {

    public DataNotExistsException() {
        super("数据不存在");
    }
    public DataNotExistsException(Object obj) {
        super("数据不存在：" + obj);
    }

}
