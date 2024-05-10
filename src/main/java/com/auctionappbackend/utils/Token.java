package com.auctionappbackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

public class Token {
    private static final String SECRET = "proyecto-final-DAM-1erAno";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    public static String generateToken(int userId, String role, String name) {
        long nowTime = System.currentTimeMillis();
        long expTime = nowTime + 1000 * 60 * 60; // 1 hora
        Date now = new Date(nowTime);
        Date exp = new Date(expTime);

        return JWT.create()
                .withIssuer("auction_app")
                .withSubject(String.valueOf(userId))
                .withAudience("auction_app_frontend")
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .withClaim("role", role)
                .withClaim("name", name)
                .sign(ALGORITHM);
    }

    public static String verifyToken(String token) {
        try {
            return JWT.require(ALGORITHM)
                    .withIssuer("auction_app")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
