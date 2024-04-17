package com.sanzuriver.oneblue.Controller;

import com.sanzuriver.oneblue.Common.Util.JsonUtil;
import com.sanzuriver.oneblue.Configuration.SystemConfigurationProperties;
import com.sanzuriver.oneblue.Service.SystemConfigService;
import jakarta.annotation.Resource;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RefreshScope
public class ConfigController {
    @Resource
    private SystemConfigService systemConfigService;
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/config")
    public Object getConfig(){
        return systemConfigService.getConfig();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/setConfig")
    public Object setConfig(@RequestBody Map<String, Object> configs){
        return systemConfigService.updateSystemConfig(configs);
    }
}
