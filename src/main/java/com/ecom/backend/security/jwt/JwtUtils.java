package com.ecom.backend.security.jwt;

import com.ecom.backend.security.Payload.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt-expiry}")
    private long jwtExpiry;
    @Value("${jwt-secret}")
    private String jwtSecret;
    @Value("${jwt-cookie}")
    private String jwtCookie;


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

    public String getTokenFromCookie(HttpServletRequest request)
    {
        Cookie cookie = WebUtils.getCookie(request,jwtCookie);
        if(cookie!=null)
        {
            return cookie.getValue();
        }

        return null;
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userDetails)
    {
        String jwt = generateToken(userDetails.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookie,jwt)
                .path("/api")
                .maxAge(Duration.ofDays(1))
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .build();
        return cookie;
    }

    public ResponseCookie cleanCookie()
    {
        ResponseCookie cookie = ResponseCookie.from(jwtCookie,"")
                .path("/api")
                .maxAge(0)
                .build();
        return cookie;
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
