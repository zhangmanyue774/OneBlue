package com.sanzuriver.oneblue.Entity.VO;

import lombok.Getter;

@Getter
public enum ResponseCodeEnums {
    SUCCESS(200, "成功"),
    FAIL(500, "失败"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源未找到"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    SERVER_ERROR(500, "服务器错误");

    private final Integer code;
    private final String message;

    ResponseCodeEnums(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
