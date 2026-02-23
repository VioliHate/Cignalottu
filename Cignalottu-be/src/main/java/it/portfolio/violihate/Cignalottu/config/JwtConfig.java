package it.portfolio.violihate.cignalottu.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Configuration
@SuppressWarnings("SpringBootApplicationProperties")
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token.expiration-ms}")
    private long refreshTokenExpirationMs;


    @PostConstruct
    public void logConfig() {
        log.info("JWT config loaded → access: {} ms, refresh: {} ms",
                accessTokenExpirationMs, refreshTokenExpirationMs);
    }
}
