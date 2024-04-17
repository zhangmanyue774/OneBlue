package com.sanzuriver.oneblue.Filter;

import com.sanzuriver.oneblue.Entity.VO.ResponseInfo;
import com.sanzuriver.oneblue.Common.Util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
/**
 * 未授权处理
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @SneakyThrows
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(JsonUtil.toJSONString(ResponseInfo.UNAUTHORIZED("当前用户没有访问权限")));
        response.getWriter().flush();
    }
}