package it.portfolio.violihate.cignalottu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.portfolio.violihate.cignalottu.dto.response.AuthResponse;
import it.portfolio.violihate.cignalottu.security.filter.JwtAuthenticationFilter;
import it.portfolio.violihate.cignalottu.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final Environment environment;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        boolean isDev = Arrays.asList(environment.getActiveProfiles()).contains("dev");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.headers(headers -> headers
                .frameOptions(frame -> {
                    if (isDev) {
                        frame.disable();
                    } else {
                        frame.sameOrigin();
                    }
                })
        );

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
                .successHandler(oAuth2AuthenticationSuccessHandler())
                .failureHandler(oAuth2AuthenticationFailureHandler())
                .permitAll()
        );

        // Logout
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .permitAll()
        );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }


    @Bean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");
            String googleId = oauthUser.getAttribute("sub");

            if (email == null) {
                response.setStatus(400);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Email non fornita da Google\"}");
                return;
            }

            AuthResponse authResponse = authService.processOAuth2User(email, name, googleId);

            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(authResponse));
        };
    }


    @Bean
    public AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Login Google fallito: " + exception.getMessage() + "\"}");
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            SecurityContextHolder.clearContext();
            response.setStatus(200);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Logout effettuato\"}");
        };
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",      // React/Vite/Angular
                "http://127.0.0.1:3000",
                "*"                           // test
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