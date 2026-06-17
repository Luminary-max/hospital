package com.bear.hospital.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static String SIGNAL = "1HU&**UUY**(GNH";
    /**
     * 生成token
     */
    public static String getToken(Map<String, String> map){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 30);             //设置过期时间为30天

        //创建jwt builder
        final JWTCreator.Builder builder = JWT.create();
        //payload
        map.forEach((k,v)->{
            builder.withClaim(k,v);
        });
        String token = builder.withExpiresAt(instance.getTime())//指定令牌过期时间
                .sign(Algorithm.HMAC256(SIGNAL));//sign
        return token;
    }

    public static DecodedJWT verify(String token){
        return JWT.require(Algorithm.HMAC256(SIGNAL)).build().verify(token);
    }

    /**
     * 刷新token：如果旧token有效且剩余有效期少于7天，则颁发新token
     */
    public static String refreshToken(String token) {
        DecodedJWT jwt;
        try {
            jwt = verify(token);
        } catch (Exception e) {
            return null;
        }
        long expiresAt = jwt.getExpiresAt().getTime();
        long now = System.currentTimeMillis();
        long remaining = expiresAt - now;
        long sevenDaysMs = 7L * 24 * 60 * 60 * 1000;
        if (remaining < sevenDaysMs) {
            Map<String, String> claims = new HashMap<>();
            jwt.getClaims().forEach((k, v) -> {
                String val = v.asString();
                if (val != null) {
                    claims.put(k, val);
                }
            });
            return getToken(claims);
        }
        return token;
    }
}
