package com.sanzuriver.oneblue.Service.Impl;

import com.sanzuriver.oneblue.Configuration.SystemConfigurationProperties;
import com.sanzuriver.oneblue.Entity.Config;
import com.sanzuriver.oneblue.Entity.VO.ResponseInfo;
import com.sanzuriver.oneblue.Mapper.SystemConfigMapper;
import com.sanzuriver.oneblue.Service.SystemConfigService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RefreshScope
public class SystemConfigServiceImpl implements SystemConfigService {
    @Resource
    private SystemConfigMapper systemConfigMapper;
    @Resource
    private ContextRefresher contextRefresher;
    @Resource
    private ConfigurableEnvironment environment;
    @Override
    public void getSystemConfig() {
        List<Config> configList = systemConfigMapper.selectAllConfig();
        Map<String, Object> configs = configList.stream()
                .collect(Collectors.toMap(Config::getKey, Config::getValue));
        environment.getPropertySources().addFirst(new MapPropertySource("newSource", configs));
        refreshConfig();
        log.info("数据库配置读取成功!");
}

    @Override
    public Object updateSystemConfig(Map<String, Object> configs) {
        systemConfigMapper.insertMultipleConfigs(configs);
        environment.getPropertySources().addFirst(new MapPropertySource("newSource", configs));
        refreshConfig();
        log.info("数据库配置更新成功!");
        return ResponseInfo.success("配置更新成功");
    }

    @Override
    public Object getConfig() {
        return systemConfigMapper.selectAllConfig().stream()
                .collect(Collectors.toMap(Config::getKey, Config::getValue));
    }
    @Resource
    SystemConfigurationProperties systemConfigurationProperties;
    @Override
    public void createSymbolicLinks() {
        List<String> sourceDirectories = systemConfigurationProperties.getMusicDirs();
        String musicFolderPath = systemConfigurationProperties.getMusicFolderPath();
        for (String sourceDirectory : sourceDirectories) {
            File[] files = new File(sourceDirectory).listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        createSymbolicLink(musicFolderPath, sourceDirectory, file.getName());
                    }
                }
            }
        }
    }
    @SneakyThrows
    private void createSymbolicLink(String musicDirectory, String sourceDirectory, String fileName){
        Path targetPath = Paths.get(sourceDirectory, fileName);
        Path linkPath = Paths.get(musicDirectory, fileName);
        if(Files.exists(linkPath)){
            Files.delete(linkPath);
        }
        Files.createSymbolicLink(linkPath, targetPath);
    }


    private void refreshConfig() {
        new Thread(() -> contextRefresher.refresh()).start();
    }
}
