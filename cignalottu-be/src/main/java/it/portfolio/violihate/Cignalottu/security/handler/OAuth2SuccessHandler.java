package it.portfolio.violihate.cignalottu.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.portfolio.violihate.cignalottu.dto.response.AuthResponse;
import it.portfolio.violihate.cignalottu.service.OAuth2UserProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2UserProcessor oAuth2UserProcessor;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        AuthResponse authResponse = oAuth2UserProcessor.processOAuth2User(
                oauthUser.getAttribute("email"),
                oauthUser.getAttribute("name"),
                oauthUser.getAttribute("sub")
        );

        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(authResponse));
    }
}