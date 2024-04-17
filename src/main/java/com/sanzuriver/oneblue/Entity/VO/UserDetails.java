package com.sanzuriver.oneblue.Entity.VO;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@Builder
public class UserDetails {
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
}
