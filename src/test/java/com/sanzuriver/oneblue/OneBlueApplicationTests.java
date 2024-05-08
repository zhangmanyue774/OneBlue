package com.sanzuriver.oneblue;

import com.sanzuriver.oneblue.Common.Util.JWTUtil;
import com.sanzuriver.oneblue.Configuration.SystemConfigurationProperties;
import com.sanzuriver.oneblue.Service.MusicInfoService;
import com.sanzuriver.oneblue.Service.MusicSourceService;
import com.sanzuriver.oneblue.Service.SystemConfigService;
import com.sanzuriver.oneblue.Service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RefreshScope
@Slf4j
class OneBlueApplicationTests {
    @Resource
    SystemConfigurationProperties systemConfigurationProperties;
    @Value("${oneblue.music-folder-path}")
    private String musicFolderPath;
    @Resource
    SystemConfigService systemConfigService;

    @Test
    void contextLoads() {
        System.out.println(musicFolderPath);
        Map<String, Object> configs = new HashMap<>();
        configs.put("oneblue.web-dav.username", "哈哈");
        systemConfigService.updateSystemConfig(configs);
        System.out.println(systemConfigurationProperties.toString());
    }

    @Test
    void test() {
        JWTUtil.verifyToken(String.valueOf(3));
    }

    @Resource
    UserService userService;

    @Test
    void test2() {
        String email = "santuchuan23@gmail.com";
        String URL = "https://2.gravatar.com/avatar/" + generateHash(email) + "?size=256";
        log.info("URL: {}", URL);
    }
    public String generateHash(String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(email.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Resource(name = "NetEase")
    private MusicSourceService musicSourceService;

    @Test
    void test3() {
        System.out.println(musicSourceService.searchMusic("周杰伦", 30, 1));
    }
    @Resource(name="webDavMusic")
    private MusicInfoService musicInfoService;
    @Test
    void test4() {
        musicInfoService.SynchronizeMusicListToDb();
    }
    @Test
    void test5() {
        systemConfigService.createSymbolicLinks();
    }
    @Test
    void test6() {
        musicSourceService.searchMusic("周杰伦", 10, 1).forEach(System.out::println);
    }
}