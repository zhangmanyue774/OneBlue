package com.sanzuriver.oneblue.Common.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JWTUtil {
    private static final long EXPIRE_TIME = 60*60*1000;// 1小时
    private static final String TOKEN_SECRET = "oneblueTokenSecret";//密钥
    /**
     * 签发token
     * @param userId 用户ID
     * @return 加密的token
     */
    @SneakyThrows
    public static String login(String userId) {
            //过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            Map<String,Object> header = new HashMap<>(2);
            header.put("Type","JWT");
            header.put("alg","HS256");
            // 返回Token字符串
            return JWT.create()
                    .withHeader(header)
                    .withClaim("userId", userId)
                    .withExpiresAt(date)
                    .sign(algorithm);
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
    public static String getUserId(String token){
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        return JWT.require(algorithm).build().verify(token).getClaim("userId").asString();
    }
}
