package it.portfolio.violihate.cignalottu.service;

import it.portfolio.violihate.cignalottu.dto.response.AuthResponse;

public interface OAuth2UserProcessor {
    AuthResponse processOAuth2User(String email, String name, String googleId);
}