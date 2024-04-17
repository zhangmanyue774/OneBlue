package com.sanzuriver.oneblue.Filter;

import com.sanzuriver.oneblue.Entity.VO.UserDetails;
import com.sanzuriver.oneblue.Service.UserService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
/**
 * JWT认证服务提供者
 */
@Component
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserService userService;

    @Override
    @SneakyThrows
    public Authentication authenticate(Authentication authentication){
        String username = String.valueOf(authentication.getPrincipal());
        String password = String.valueOf(authentication.getCredentials());

        UserDetails userDetails = userService.loadUserByUsername(username);
        if(passwordEncoder.matches(password,userDetails.getPassword())){
            return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
        }

        throw new BadCredentialsException("Error!!");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
