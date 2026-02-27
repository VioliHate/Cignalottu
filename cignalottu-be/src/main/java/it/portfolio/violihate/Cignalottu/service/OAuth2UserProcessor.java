package it.portfolio.violihate.cignalottu.service;

import it.portfolio.violihate.cignalottu.dto.response.AuthResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserProcessor {
    AuthResponse processOAuth2User(OAuth2User oauthUser);
}