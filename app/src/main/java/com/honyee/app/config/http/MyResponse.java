package com.honyee.app.config.http;


/**
 * 统一返回结构.
 *
 */
public class MyResponse<T> {

    private Integer code = MyResponseCodeEnums.OK.getCode();
    private String message;
    private T data;

    private String causeBy = "";

    public String getCauseBy() {
        return causeBy;
    }

    public void setCauseBy(String causeBy) {
        this.causeBy = causeBy;
    }

    public MyResponse() {}

    public MyResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public MyResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public MyResponse(Integer code, String message, T data, String causeBy) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.causeBy = causeBy;
    }

    /**
     * 成功返回静态方法.
     */
    public static <T> MyResponse<T> ok(String msg, T data) {
            return new MyResponse<>(MyResponseCodeEnums.OK.getCode(), msg, data);
    }

    /**
     * 成功返回静态方法.
     *
     */
    public static MyResponse<Object> ok() {
        return new MyResponse<>(MyResponseCodeEnums.OK.getCode(), MyResponseCodeEnums.OK.getMessage(), null);
    }
    /**
     * 成功返回静态方法.
     */
    public static <T> MyResponse<T> ok(T data) {
        return new MyResponse<>(MyResponseCodeEnums.OK.getCode(), MyResponseCodeEnums.OK.getMessage(), data);
    }

    /**
     * 调用失败
     */
    public static <T> MyResponse<T> fail() {
        return new MyResponse<>(MyResponseCodeEnums.COMMON_EXCEPTION.getCode(), MyResponseCodeEnums.COMMON_EXCEPTION.getMessage(), null);
    }

    /**
     * 调用失败
     */
    public static <T> MyResponse<T> fail(String msg, T data) {
        return new MyResponse<>(MyResponseCodeEnums.COMMON_EXCEPTION.getCode(), msg, data);
    }

    @Override
    public String toString() {
        return "MyResponse{" +
            "code=" + code +
            ", message='" + message + '\'' +
            ", data=" + data +
            ", causeBy='" + causeBy + '\'' +
            '}';
    }

    public Integer getCode() {
        return code;
    }

    public boolean isSuccess(){
        return code == MyResponseCodeEnums.OK.getCode();
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
