package it.portfolio.violihate.cignalottu.dto.response;



public record AuthResponse (
     String accessToken, // JWT
     String refreshToken,
     String tokenType,       // "Bearer"
     Long userId,
     String email,
     String role //role as string
){
}
