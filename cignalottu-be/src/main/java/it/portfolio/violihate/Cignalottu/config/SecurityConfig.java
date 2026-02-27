package it.portfolio.violihate.cignalottu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.portfolio.violihate.cignalottu.security.CookieOAuth2AuthorizationRequestRepository;
import it.portfolio.violihate.cignalottu.security.filter.JwtAuthenticationFilter;
import it.portfolio.violihate.cignalottu.security.handler.LogoutSuccessHandlerImpl;
import it.portfolio.violihate.cignalottu.security.handler.OAuth2FailureHandler;
import it.portfolio.violihate.cignalottu.security.handler.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final Environment environment;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final LogoutSuccessHandlerImpl logoutSuccessHandler;



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2SuccessHandler oAuth2SuccessHandler) throws Exception {
        boolean isDev = Arrays.asList(environment.getActiveProfiles()).contains("dev");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).sessionFixation().migrateSession()  // migra sessione su auth per prevenire fixation attacks
                        .invalidSessionUrl("/login")
                );

        http.headers(headers -> {
            if (isDev) {
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
            } else {
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
            }
        });

        // Autorizzazioni
        http.authorizeHttpRequests(auth -> {
            if (isDev) {
                auth.anyRequest().permitAll();
            } else {
                auth
                        // Pubblici
                        .requestMatchers("/api/auth/**").permitAll()
                        // OAuth2 Google
                        .requestMatchers("/oauth2/**", "/login/oauth2/code/**").permitAll()
                        // Logout
                        .requestMatchers("/logout").permitAll()
                        // Tutto il resto protetto
                        .anyRequest().authenticated();
            }
        });

        // OAuth2 Login (Google)
        http.oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
                .authorizationEndpoint(auth -> auth
                        .authorizationRequestRepository(new CookieOAuth2AuthorizationRequestRepository())
                )
                .permitAll()
        );

        // Logout
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .permitAll()
        );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",      // React/Vite/Angular
                "http://127.0.0.1:3000"// test
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}