package com.sanzuriver.oneblue.Service.Impl;

import com.sanzuriver.oneblue.Entity.Role;
import com.sanzuriver.oneblue.Entity.User;
import com.sanzuriver.oneblue.Entity.UserRole;
import com.sanzuriver.oneblue.Entity.VO.ResponseInfo;
import com.sanzuriver.oneblue.Entity.VO.UserDetails;
import com.sanzuriver.oneblue.Mapper.UserMapper;
import com.sanzuriver.oneblue.Service.UserService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class UserServiceImpl implements UserService{
    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder bCryptPasswordEncoder;
    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(String userId) {
        User user = userMapper.selectUser(userId);
        Role role = userMapper.selectUserRole(user.getId());
        log.info("role: {}", role);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));
        log.info("authorities: {}", authorities);
        return UserDetails.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
    @Override
    public Integer login(User user) {
        User dbUser = userMapper.login(user.getEmail());
        if(isPasswordMatch(user.getPassword(), dbUser.getPassword())){
            return dbUser.getId();
        }
        return null;
    }
    @Override
    public Object register(User user,Integer role) {
        user.setPassword(encodePassword(user.getPassword()));
        try {
            userMapper.insertUser(user);
        }
        catch (Exception e) {
            log.error("Register failed: {}", e.getMessage());
            return ResponseInfo.fail("用户信息已存在");
        }
        userMapper.insertUserRole(UserRole.builder().userId(user.getId()).roleId(role).build());
        return ResponseInfo.success("注册成功");
    }

    @Override
    public void insertAdminUser(User user) {
        user.setPassword(encodePassword(user.getPassword()));
        if(userMapper.insertAdminUser(user).equals(1)){
            userMapper.insertUserRole(UserRole.builder().userId(1).roleId(1).build());
            log.info("用户角色分配成功");
        }
        log.info("Admin初始化成功!");

    }

    public boolean isPasswordMatch(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
    public String encodePassword(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }
}
