package it.portfolio.violihate.cignalottu.service;

import it.portfolio.violihate.cignalottu.dto.response.AuthResponse;
import it.portfolio.violihate.cignalottu.entity.Provider;
import it.portfolio.violihate.cignalottu.entity.Role;
import it.portfolio.violihate.cignalottu.entity.User;
import it.portfolio.violihate.cignalottu.repository.UserRepository;
import it.portfolio.violihate.cignalottu.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserProcessor{

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");


    @Override
    @Transactional
    public AuthResponse processOAuth2User(String email, String name, String googleId) {
        String normalizedEmail = email.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Email non valida da Google");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(normalizedEmail);
                    newUser.setPassword(null);
                    newUser.setFirstName(name != null && !name.trim().isEmpty() ? name.trim() : "Utente Google");
                    newUser.setLastName("");
                    newUser.setRole(Role.CUSTOMER);
                    newUser.setProvider(Provider.GOOGLE);
                    newUser.setProviderId(googleId);
                    newUser.setCreatedAt(LocalDateTime.now());
                    newUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(newUser);
                });

        if (!Provider.GOOGLE.equals(user.getProvider())) {
            log.info("Merge account: {} era {}, ora GOOGLE", normalizedEmail, user.getProvider());
            user.setProvider(Provider.GOOGLE);
            user.setProviderId(googleId);
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String accessToken = jwtService.generateAccessToken(auth);
        String refreshToken = jwtService.generateRefreshToken(auth);

        log.debug("OAuth2 token generati per: {}", normalizedEmail);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole().name());
    }
}
