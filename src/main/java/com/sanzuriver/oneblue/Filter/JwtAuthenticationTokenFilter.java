package com.sanzuriver.oneblue.Filter;

import com.sanzuriver.oneblue.Entity.VO.UserDetails;
import com.sanzuriver.oneblue.Service.UserService;
import com.sanzuriver.oneblue.Common.Util.JWTUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Objects;

@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final static String AUTH_HEADER = "Authorization";
    private final static String AUTH_HEADER_TYPE = "Bearer";

    @Resource
    private UserService userService;

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain){
        // get token from header:  Authorization: Bearer <token>
        String authHeader = request.getHeader(AUTH_HEADER);
        if (Objects.isNull(authHeader) || !authHeader.startsWith(AUTH_HEADER_TYPE)){
            filterChain.doFilter(request,response);
            return;
        }

        String authToken = authHeader.split(" ")[1];
        log.info("authToken:{}" , authToken);
        //verify token
        try {
            if(!JWTUtil.verifyToken(authToken)){
                filterChain.doFilter(request,response);
                return;
            }
        } catch (Exception e) {
            filterChain.doFilter(request,response);
            return;
        }

        final String userId = JWTUtil.getUserId(authToken);
        UserDetails userDetails = userService.loadUserByUsername(userId);

        // 注意，这里使用的是3个参数的构造方法，此构造方法将认证状态设置为true
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, userDetails.getPassword(), userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        //将认证过了凭证保存到security的上下文中以便于在程序中使用
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
