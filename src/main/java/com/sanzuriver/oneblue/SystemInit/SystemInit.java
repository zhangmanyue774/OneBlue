package com.sanzuriver.oneblue.SystemInit;

import com.sanzuriver.oneblue.Entity.User;
import com.sanzuriver.oneblue.Service.MusicInfoService;
import com.sanzuriver.oneblue.Service.SystemConfigService;
import com.sanzuriver.oneblue.Service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SystemInit  implements CommandLineRunner{
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private UserService userService;
    @Value("${oneblue.username}")
    private String username;
    @Value("${oneblue.password}")
    private String password;
    @Override
    public void run(String... args){
        userService.insertAdminUser(User.builder().email(username).password(password).build());
        systemConfigService.getSystemConfig();
    }
}
