package com.sanzuriver.oneblue.Controller.User;

import com.sanzuriver.oneblue.Common.Util.GravatarUtil;
import com.sanzuriver.oneblue.Entity.User;
import com.sanzuriver.oneblue.Entity.VO.ResponseInfo;
import com.sanzuriver.oneblue.Entity.VO.UserVo;
import com.sanzuriver.oneblue.Service.UserService;
import com.sanzuriver.oneblue.Common.Util.JWTUtil;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    UserService userService;
    @PostMapping("/login")
    public Object login(@RequestBody User user) {
        Integer userID = userService.login(user);
        if (userID == null) {
            return ResponseInfo.UNAUTHORIZED("Login failed");
        }
        return UserVo.builder()
                .Avatar(GravatarUtil.getGravatarUrl(user.getEmail()))
                .email(user.getEmail())
                .access_token(JWTUtil.login(String.valueOf(userID)).get("access_token"))
                .refresh_token(JWTUtil.login(String.valueOf(userID)).get("refresh_token"))
                .build();
    }
    @PostMapping("/register")
    public Object register(@RequestBody User user) {
        user.setId(new Random().nextInt(9000)+1000);
        return userService.register(user,2);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createUser/{role}")
    public Object createUser(@RequestBody User user, @PathVariable("role") Integer role) {
        user.setId(new Random().nextInt(9000)+1000);
        return userService.register(user, role);
    }
    @PostMapping("/refresh_token")
    public Object refreshToken(@RequestBody Map<String,String> refreshToken) {
        String token = refreshToken.get("refresh_token");
        try {
            if(JWTUtil.verifyRefreshToken(token)) {
                return JWTUtil.login(JWTUtil.getRefreshUserId(token));
            }
        } catch (Exception e) {
            return ResponseInfo.UNAUTHORIZED("refresh_token is invalid");
        }
        return ResponseInfo.UNAUTHORIZED("refresh_token is invalid");
    }
}
