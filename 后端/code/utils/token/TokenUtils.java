package com.elegrp.contract.util.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class TokenUtils {

    private static final String SECRET = "ELEGRPCONTRACTVALIDITYMANAGEMENTSYSTEM";

    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());


    /**
     * 生成 token
     */
    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .signWith(key)
                .compact();
    }

    /**
     * 解析 token
     */
    public static Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUserIdFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
}
