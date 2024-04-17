package com.sanzuriver.oneblue.Exception;

import com.sanzuriver.oneblue.Entity.VO.ResponseCodeEnums;
import com.sanzuriver.oneblue.Entity.VO.ResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.FileNotFoundException;

@Slf4j
@RestControllerAdvice(basePackages = "com.sanzuriver.oneblue.Controller")
public class GlobalHandler implements ResponseBodyAdvice<Object> {
    @ExceptionHandler(Exception.class)
    public ResponseInfo<String> exceptionHandler(Exception e) {
        log.error(e.getMessage());
        return new ResponseInfo<>(ResponseCodeEnums.SERVER_ERROR);
    }
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseInfo<String> validationExceptionHandler(HandlerMethodValidationException e) {
        log.error(e.getMessage());
        return new ResponseInfo<>(ResponseCodeEnums.PARAM_ERROR);
    }
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseInfo<String> constraintViolationExceptionHandler(ConstraintViolationException e) {
        log.error(e.getMessage());
        return new ResponseInfo<>(ResponseCodeEnums.NOT_FOUND);
    }
    @ExceptionHandler(NullPointerException.class)
    public ResponseInfo<String> nullPointerExceptionHandler(NullPointerException e) {
        log.error(e.getMessage());
        return new ResponseInfo<>(ResponseCodeEnums.NOT_FOUND);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseInfo<String> runtimeExceptionHandler(RuntimeException e) {
        log.error(e.getMessage());
        return new ResponseInfo<>(ResponseCodeEnums.SERVER_ERROR);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseInfo<String> accessDeniedExceptionHandler(AccessDeniedException e) {
        log.error(e.getMessage());
        return ResponseInfo.UNAUTHORIZED("当前用户没有访问权限");
    }
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (body == null  || body instanceof ResponseInfo || body instanceof Exception || body instanceof String || body instanceof byte[]) {
            return body == null ? ResponseInfo.success() : body;
        }
        return ResponseInfo.success(body);
    }
}
