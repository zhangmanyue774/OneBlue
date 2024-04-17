package com.sanzuriver.oneblue.Filter;

import com.sanzuriver.oneblue.Entity.VO.ResponseInfo;
import com.sanzuriver.oneblue.Common.Util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
/**
 * 未登录处理
 */
@Component
public class CustomUnauthorizedHandler implements AuthenticationEntryPoint {
    @Override
    @SneakyThrows
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(JsonUtil.toJSONString(ResponseInfo.UNAUTHORIZED("未登录或登录已过期，请重新登录！")));
        response.getWriter().flush();
    }
}
