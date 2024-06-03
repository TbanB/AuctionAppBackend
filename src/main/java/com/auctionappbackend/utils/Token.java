package com.auctionappbackend.utils;

import com.auctionappbackend.model.TokenDetail;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class Token {
    private static final String SECRET = "proyecto-final-DAM-1erAno";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    public static String generateToken(int userId, String role, String name) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long expMillis = nowMillis + 604800016;
        Date exp = new Date(expMillis);

        System.out.println(">>> 1: " + userId);
        System.out.println(">>> 2: " + role);
        System.out.println(">>> 3: " + name);
        
        String token = JWT.create()
                .withIssuer("auction_app")
                .withAudience("auction_app_frontend")
                .withClaim("role", role)
                .withClaim("name", name)
                .withClaim("userId", userId)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(ALGORITHM);

        return token;
    }

    public static TokenDetail verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(ALGORITHM)
                    .withIssuer("auction_app")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            int userId = jwt.getClaim("userId").asInt();
            String role = jwt.getClaim("role").asString();
            return new TokenDetail(userId, role);
        } catch (TokenExpiredException tee) {
            System.out.println("Token has expired: " + tee.getMessage());
            return null;
        } catch (JWTVerificationException jve) {
            System.out.println("Token verification failed: " + jve.getMessage());
            return null;
        }
    }
}
