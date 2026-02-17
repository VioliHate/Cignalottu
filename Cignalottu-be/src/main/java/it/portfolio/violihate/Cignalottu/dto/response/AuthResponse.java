package it.portfolio.violihate.cignalottu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;     // JWT
    private String tokenType;       // "Bearer"
    private Long userId;
    private String email;
    private String role;            // nome del ruolo come stringa
}
