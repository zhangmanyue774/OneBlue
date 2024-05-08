package com.sanzuriver.oneblue.Common.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JWTUtil {
    private static final long EXPIRE_TIME = 60*60*1000;// 1小时
    private static final long REFRESH_EXPIRE_TIME = 60*60*24*3*1000;// 3天
    private static final String TOKEN_SECRET = "oneblueTokenSecret";//密钥
    private static final String REFRESH_SECRET = "oneblueRefreshTokenSecret";//密钥
    /**
     * 签发token
     * @param userId 用户ID
     * @return 加密的token
     */
    @SneakyThrows
    public static Map<String, String> login(String userId) {
            //过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Date refreshDate = new Date(System.currentTimeMillis() + REFRESH_EXPIRE_TIME);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            Algorithm refreshAlgorithm = Algorithm.HMAC256(REFRESH_SECRET);
            Map<String,Object> header = new HashMap<>(2);
            header.put("Type","JWT");
            header.put("alg","HS256");
            // 返回Token字符串
            Map<String,String> map = new HashMap<>(2);
            map.put("access_token",JWT.create()
                    .withHeader(header)
                    .withClaim("userId", userId)
                    .withExpiresAt(date)
                    .sign(algorithm));
            map.put("refresh_token",JWT.create()
                    .withHeader(header)
                    .withClaim("userId", userId)
                    .withExpiresAt(refreshDate)
                    .sign(refreshAlgorithm));
            return map;
    }
    /**
     * 验证token
     * @param token 密钥
     * @return 是否正确
     */
    public static boolean verifyToken(String token){
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        // 验证token是否符合规则
        try {
            JWT.require(algorithm).build().verify(token);
        } catch (Exception e) {
            log.error("Token验证失败");
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }
    public static boolean verifyRefreshToken(String token){
        Algorithm algorithm = Algorithm.HMAC256(REFRESH_SECRET);
        // 验证token是否符合规则
        try {
            JWT.require(algorithm).build().verify(token);
        } catch (Exception e) {
            log.error("RefreshToken验证失败");
            throw new RuntimeException(e.getMessage());
        }
        return true;
    }
    public static String getUserId(String token){
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        return JWT.require(algorithm).build().verify(token).getClaim("userId").asString();
    }
    public static String getRefreshUserId(String token){
        Algorithm algorithm = Algorithm.HMAC256(REFRESH_SECRET);
        return JWT.require(algorithm).build().verify(token).getClaim("userId").asString();
    }
}
