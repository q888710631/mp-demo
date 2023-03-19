package com.mp.config;


import org.slf4j.MDC;

/**
 * 统一返回结构.
 *
 */
public class MyResponse<T> {

    private final static String SUCCESS = "success";

    private Integer code;
    private String message;
    private T data;

    private String causeBy;

    private String traceId;

    public String getTraceId() {
        return MDC.get("trace_id");
    }

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
     *
     * @param msg  提示消息
     * @param data body数据
     * @param <T>  泛型
     * @return
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
     *
     * @param data body数据
     * @param <T>  泛型
     * @return
     */
    public static <T> MyResponse<T> ok(T data) {
        return new MyResponse<>(200, SUCCESS, data);
    }

    /**
     * 调用失败, 返回500.
     *
     * @param msg  提示下戏
     * @param data body数据
     * @param <T>  泛型
     * @return MyResponse
     */
    public static <T> MyResponse<T> fail(String msg, T data) {
        return new MyResponse<>(500, msg, data);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MyResponse{");
        sb.append("code=").append(code);
        sb.append(", msg='").append(message).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
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
