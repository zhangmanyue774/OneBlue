package com.sanzuriver.oneblue.Entity.VO;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ResponseInfo<T>{
    private Integer code;
    private String message;
    private T data;

    public ResponseInfo(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseInfo(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static <T> ResponseInfo<T> success() {
        return new ResponseInfo<>();
    }

    public static <T> ResponseInfo<T> success(T data) {
        return new ResponseInfo<>(data);
    }
    public static <T> ResponseInfo<T> fail(String message) {
        return new ResponseInfo<>(ResponseCodeEnums.FAIL.getCode(), message);
    }
    public static <T> ResponseInfo<T> UNAUTHORIZED(String message) {
        return new ResponseInfo<>(ResponseCodeEnums.UNAUTHORIZED.getCode(),message);
    }

    public ResponseInfo() {
        this.code = ResponseCodeEnums.SUCCESS.getCode();
        this.message = ResponseCodeEnums.SUCCESS.getMessage();
    }

    public ResponseInfo(ResponseCodeEnums statusEnums) {
        this.code = statusEnums.getCode();
        this.message = statusEnums.getMessage();
    }

    /**
     * 若没有数据返回，可以人为指定状态码和提示信息
     */
    public ResponseInfo(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    /**
     * 有数据返回时，状态码为200，默认提示信息为“操作成功！”
     */
    public ResponseInfo(T data) {
        this.data = data;
        this.code = ResponseCodeEnums.SUCCESS.getCode();
        this.message = ResponseCodeEnums.SUCCESS.getMessage();
    }

    /**
     * 有数据返回，状态码为 200，人为指定提示信息
     */
    public ResponseInfo(T data, String msg) {
        this.data = data;
        this.code = ResponseCodeEnums.SUCCESS.getCode();
        this.message = msg;
    }
}
