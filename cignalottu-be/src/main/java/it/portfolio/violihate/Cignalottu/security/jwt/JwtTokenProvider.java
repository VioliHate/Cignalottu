package it.portfolio.violihate.cignalottu.security.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private static final String AUTHORITIES_KEY = "roles";

    @Value("${jwt.secret}")
    private String jwtSecretBase64;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private SecretKey secretKey;


    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);

        if (keyBytes.length < 32) {
            throw new IllegalStateException("Key JWT to short!");
        }
    }

    public String generateToken(Authentication authentication) {

        String email = authentication.getName();

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }


    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformed: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT not supported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Generic error validation JWT: {}", e.getMessage());
        }
        return false;
    }

    private Claims getClaimsFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
        return parser.parseClaimsJws(token).getBody();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String rolesStr = claims.get("roles", String.class);
        if (rolesStr == null || rolesStr.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(rolesStr.split(","))
                .map(String::trim)
                .toList();
    }

    // Only for test
    public SecretKey getSecretKeyForTesting() {
        return secretKey;
    }

    public Long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}

