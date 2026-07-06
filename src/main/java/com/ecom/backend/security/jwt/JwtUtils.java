package com.ecom.backend.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt-expiry}")
    private long jwtExpiry;
    @Value("${jwt-secret}")
    private String jwtSecret;


    public SecretKey key()
    {
        return (SecretKey) Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret));
    }

    public String generateToken(String username)
    {
        return Jwts.builder()
                .signWith(key())
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiry*1000))
                .compact();
    }

    public boolean valid(String token) {
        Date expiry = Jwts.parser()
                .verifyWith(key()).build()
                .parseSignedClaims(token)
                .getPayload().getExpiration();
        return expiry.after(new Date());
    }

    public String extractUsername(String token)
    {
        return Jwts.parser()
                .verifyWith(key()).build()
                .parseSignedClaims(token).getPayload()
                .getSubject();
    }
}
