package com.honyee.app.config.http;


/**
 * 统一返回结构.
 *
 */
public class MyResponse<T> {

    private final static String SUCCESS = "success";

    private Integer code = 200;
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
        return new MyResponse<>(200, msg, data);
    }

    /**
     * 成功返回静态方法.
     *
     */
    public static MyResponse<Object> ok() {
        return new MyResponse<>(200, SUCCESS, null);
    }
    /**
     * 成功返回静态方法.
     */
    public static <T> MyResponse<T> ok(T data) {
        return new MyResponse<>(200, SUCCESS, data);
    }

    /**
     * 调用失败, 返回500.
     */
    public static <T> MyResponse<T> fail(String msg, T data) {
        return new MyResponse<>(500, msg, data);
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
        return code == 200;
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
