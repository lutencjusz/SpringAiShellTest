package com.example.SpringDocumentationAI.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.jsonwebtoken.Jwts.*;

/**
 * Klasa JwtService służy do obsługi tokenów JWT. Aby wygenerować SECRET należy użyć testu JwtSecretMaker.
 */
@Service
public class JwtService {
    public static final String SECRET = "8855DD1CE5D578CCDF00126E5D776C892BF9ECC7DF64672621C67EF92F47FD1E";

    private static final Long tokenValidity = TimeUnit.HOURS.toMillis(1);

    public String generateToken(UserDetails userDetails) {
        Map<String, String> claims = new HashMap<>();
        claims.put("iss", "https://secure.genuinecoder.com");
        return builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(new Date().toInstant()))
                .expiration(Date.from(new Date().toInstant().plusMillis(tokenValidity)))
                .signWith(getSecretKey())
                .compact();
    }

    private SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    public String extractUsername(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getSubject();
    }

    private Claims getClaims(String jwt) {
        if (jwt == null || jwt.split("\\.").length != 3) {
            throw new IllegalArgumentException("Niepoprawny format JWT");
        }
        return Jwts.parser()
                .setSigningKey(getSecretKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        Claims claims = getClaims(jwt);
        return claims.getExpiration().after(Date.from(Instant.now()));
    }
}