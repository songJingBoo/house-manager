package com.song.demo.util;

import com.song.demo.vo.LoginVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtUtils {
    /**
     * 过期时间 30分钟
     */
    private static final long EXPIRE_TIME = 30 * 60 * 1000;

    /**
     * 密钥
     */
    private static final String KEY = "song!@#123";

    public static LoginVo createToken(String username, String userId) {
        // 当前时间
        final Date now = new Date();
        // 过期时间
        final Date expirationDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);

        // 其他用户信息
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, KEY)
                .compact();

        return LoginVo.builder().username(username).userId(userId).token(token).expirationTime(expirationDate).build();
    }

    /**
     * 验证token是否有效
     */
    public static boolean validateToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null;
    }

    /**
     * 从Token中获取负载信息
     */
    public static Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(KEY)
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println(claims);
        } catch (ExpiredJwtException e) {
            log.error("JWT过期", e);
        } catch (Exception e) {
            log.error("JWT解析失败", e);
        }
        return claims;
    }

    public static String getSubjectFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return claims.getSubject();
    }
}
