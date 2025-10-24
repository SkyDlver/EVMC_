package com.mycompany.evmc.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** ✅ New method that accepts extra claims */
    public String generateToken(Map<String, Object> extraClaims, String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + (1000 * 60 * 60 * 24)); // 24h

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSignKey())
                .compact();
    }

    /** Simplified overload — no extra claims */
    public String generateToken(String subject) {
        return generateToken(Map.of(), subject);
    }

    private Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build();

        return parser
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date()) && claims.getSubject() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
